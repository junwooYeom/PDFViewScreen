package com.example.sling.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toFile
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.example.sling.extension.toDateString
import com.example.sling.ui.viewmodel.BookDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    viewModel: BookDetailViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    bookId: String
) {

    val context = LocalContext.current
    val imageLoader = context.imageLoader
    val currentBook by viewModel.currentBook.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentTickTime by viewModel.currentTime.collectAsState()

    val rendererScope = rememberCoroutineScope()
    val scrollScope = rememberCoroutineScope()
    val mutex = remember { Mutex() }

    var pageSelectDialog by remember {
        mutableStateOf(false)
    }
    val lazyListState = rememberLazyListState()
    val currentPage = snapshotFlow {
        lazyListState.firstVisibleItemIndex
    }.collectAsState(initial = 0)

    LaunchedEffect(key1 = bookId) {
        viewModel.getBookById(bookId)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            viewModel.tickTime(currentTickTime + 1)
        }
    }

    BackHandler {
        viewModel.updateStudyTime(bookId, currentTickTime)
        onBackPressed()
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    } else {
        currentBook?.let { it ->
            val fileUri = Uri.parse(it.url)
            val renderer by produceState<PdfRenderer?>(null, fileUri) {
                rendererScope.launch(Dispatchers.IO) {
                    val input =
                        ParcelFileDescriptor.open(
                            fileUri.toFile(),
                            ParcelFileDescriptor.MODE_READ_ONLY
                        )
                    value = PdfRenderer(input)
                }
                awaitDispose {
                    val currentRenderer = value
                    rendererScope.launch(Dispatchers.IO) {
                        mutex.withLock {
                            currentRenderer?.close()
                        }
                    }
                }
            }
            val pageCount by remember {
                derivedStateOf { renderer?.pageCount ?: 0 }
            }
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                                Text(text = currentTickTime.toDateString())
                        },
                        navigationIcon = {
                            Icon(
                                modifier = Modifier.clickable {
                                    pageSelectDialog = true
                                },
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search Page"
                            )
                        },
                        actions = {
                            if (it.bookmarks.contains(currentPage.value)) {
                                Text(text = "북마크됨")
                            } else {
                                Text(
                                    modifier = Modifier.clickable {
                                        viewModel.bookmarkItem(bookId, currentPage.value)
                                    },
                                    text = "북마크하기"
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->
                PDFRenderScreen(
                    modifier = Modifier.padding(paddingValues),
                    bookUri = Uri.parse(it.url),
                    mutex = mutex,
                    context = context,
                    imageLoader = imageLoader,
                    renderer = renderer,
                    pageCount = pageCount,
                    listState = lazyListState
                )
            }

            if (pageSelectDialog) {
                Dialog(
                    onDismissRequest = { pageSelectDialog = false }
                ) {
                    PDFPageScreen(
                        bookmarkList = it.bookmarks,
                        bookUri = Uri.parse(it.url),
                        mutex = mutex,
                        context = context,
                        imageLoader = imageLoader,
                        renderer = renderer,
                        onPageClicked = {
                            scrollScope.launch {
                                pageSelectDialog = false
                                lazyListState.animateScrollToItem(it)
                            }
                        }
                    )
                }
            }
        } ?: EmptyView()
    }
}

@Composable
private fun PDFRenderScreen(
    modifier: Modifier = Modifier,
    bookUri: Uri,
    mutex: Mutex,
    context: Context,
    imageLoader: ImageLoader,
    renderer: PdfRenderer?,
    pageCount: Int,
    listState: LazyListState
) {

    val imageLoadingScope = rememberCoroutineScope()
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val width = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
        val height = (width * sqrt(2f)).toInt()
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = listState
        ) {
            items(
                count = pageCount,
                key = { index -> "$bookUri-$index" }
            ) { index ->
                val cacheKey = MemoryCache.Key("$bookUri-$index")
                var bitmap by remember { mutableStateOf(imageLoader.memoryCache?.get(cacheKey) as? Bitmap?) }
                if (bitmap != null) {
                    val request = ImageRequest.Builder(context)
                        .size(width, height)
                        .memoryCacheKey(cacheKey)
                        .data(bitmap)
                        .build()
                    Image(
                        modifier = Modifier
                            .background(Color.Black)
                            .aspectRatio(1f / sqrt(2f))
                            .fillMaxWidth(),
                        contentScale = ContentScale.Fit,
                        painter = rememberAsyncImagePainter(model = request),
                        contentDescription = "Page ${index + 1} of $pageCount"
                    )
                } else {
                    DisposableEffect(bookUri, index) {
                        val job = imageLoadingScope.launch(Dispatchers.IO) {
                            val destinationBitmap =
                                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                            mutex.withLock {
                                if (!coroutineContext.isActive) return@launch
                                try {
                                    renderer?.let {
                                        it.openPage(index).use { page ->
                                            page.render(
                                                destinationBitmap,
                                                null,
                                                null,
                                                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                                            )
                                        }
                                    }
                                } catch (e: Exception) {
                                    return@launch
                                }
                            }
                            bitmap = destinationBitmap
                        }

                        onDispose {
                            job.cancel()
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(Color.White)
                            .aspectRatio(1f / sqrt(2f))
                            .fillMaxWidth()
                    )
                }

            }
        }
    }
}

@Composable
private fun EmptyView() {

}