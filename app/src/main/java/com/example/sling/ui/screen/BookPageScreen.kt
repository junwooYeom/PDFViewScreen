package com.example.sling.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.memory.MemoryCache
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PDFPageScreen(
    bookmarkList: List<Int>,
    bookUri: Uri,
    mutex: Mutex,
    context: Context,
    imageLoader: ImageLoader,
    renderer: PdfRenderer?,
    onPageClicked: (pageIndex: Int) -> Unit,
) {
    val imageLoadingScope = rememberCoroutineScope()
    var isShowOnlyBookmark by remember {
        mutableStateOf(false)
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        Text(
                            modifier = Modifier.clickable {
                                isShowOnlyBookmark = isShowOnlyBookmark.not()
                            },
                            text = if (isShowOnlyBookmark) {
                                "전체"
                            } else {
                                "북마크만"
                            }
                        )
                    },
                    title = {
                        Text(text = "탐색")
                    },
                )
            },
        ) {
            Column(
                modifier = Modifier.padding(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyVerticalGrid(
                    modifier = Modifier.padding(16.dp),
                    columns = GridCells.Adaptive(120.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        count = if (isShowOnlyBookmark) {
                            bookmarkList.size
                        } else {
                            renderer?.pageCount ?: 0
                        },
                        key = { index -> "$bookUri-$index" },
                    ) { index ->
                        val currentIndex =
                            if (isShowOnlyBookmark) bookmarkList[index] else index
                        Column(
                            modifier = Modifier.clickable { onPageClicked(currentIndex) },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            val cacheKey =
                                MemoryCache.Key("$bookUri-${currentIndex}")

                            var bitmap by remember {
                                mutableStateOf(
                                    imageLoader.memoryCache?.get(
                                        cacheKey
                                    ) as? Bitmap?
                                )
                            }
                            if (bitmap != null) {
                                val request = ImageRequest.Builder(context)
                                    .size(120, 160)
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
                                    contentDescription = "Page $currentIndex"
                                )
                            } else {
                                DisposableEffect(bookUri, currentIndex) {
                                    val job = imageLoadingScope.launch(Dispatchers.IO) {
                                        val destinationBitmap =
                                            Bitmap.createBitmap(
                                                120,
                                                160,
                                                Bitmap.Config.ARGB_8888
                                            )
                                        mutex.withLock {
                                            if (!coroutineContext.isActive) return@launch
                                            try {
                                                renderer?.let { renderer ->
                                                    renderer.openPage(currentIndex).use { page ->
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
                            if (bookmarkList.contains(index)) {
                                Text(
                                    textAlign = TextAlign.Center,
                                    text = "${currentIndex + 1}  (북마크됨)"
                                )
                            } else {
                                Text(
                                    textAlign = TextAlign.Center,
                                    text = "${currentIndex + 1}"
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}


