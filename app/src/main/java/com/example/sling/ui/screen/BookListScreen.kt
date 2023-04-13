package com.example.sling.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.sling.domain.model.Book
import com.example.sling.ui.viewmodel.BookListViewModel

@Composable
fun BookListScreen(
    viewModel: BookListViewModel = hiltViewModel(),
    onAddClicked: () -> Unit,
    onItemClicked: (bookId: String) -> Unit,
) {
    val studiedBooks by viewModel.bookList.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        if (studiedBooks.isNotEmpty()) {
            BookListWithStudyFilter(
                bookList = studiedBooks,
                onItemClicked = onItemClicked,
                onAddClicked = onAddClicked,
                onClearButtonClicked = {
                    viewModel.clearAllEntities()
                }
            )
        } else {
            EmptyBookListComponent(onAddClicked)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BookListWithStudyFilter(
    bookList: List<Book>,
    onItemClicked: (bookId: String) -> Unit,
    onAddClicked: () -> Unit,
    onClearButtonClicked: () -> Unit
) {
    val groupedBookList = bookList.groupBy { it.isStudying }
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        groupedBookList.forEach { (manufacturer, models) ->
            stickyHeader {
                Text(
                    text = if (manufacturer) {
                        "학습 중"
                    } else {
                        "학습 전"
                    },
                    style = Typography().headlineMedium
                )
            }
            items(models) {
                BookItemComponent(
                    book = it,
                    onItemClicked = onItemClicked
                )
            }
        }
        item {
            AddBookComponent(onAddClicked = onAddClicked)
        }
        item {
            Row {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    modifier = Modifier.padding(vertical = 12.dp),
                    onClick = onClearButtonClicked
                ) {
                    Text(text = "전부 삭제하기")
                }
            }
        }
    }
}

@Composable
private fun AddBookComponent(
    modifier: Modifier = Modifier,
    onAddClicked: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        modifier = modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = onAddClicked
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "더 배우고 싶은 책을 추가해주세요!")
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier.padding(vertical = 12.dp),
                onClick = onAddClicked
            ) {
                Text(text = "추가하기")
            }
        }
    }
}

@Composable
private fun BookItemComponent(
    book: Book,
    onItemClicked: (bookId: String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color.DarkGray),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = {
                    onItemClicked(book.id)
                }
            )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                modifier = Modifier
                    .width(120.dp)
                    .height(160.dp),
                model = book.thumbnailUrl,
                contentDescription = "thumbnail"
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.fillMaxHeight().weight(1f, false),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = book.title,
                    style = Typography().headlineMedium
                )
                Text(
                    text = if (book.time != null && book.time > 0) {
                        "${book.time / 3600}시간 ${book.time / 60}분 ${book.time % 60}초간 학습 중"
                    } else {
                        "학습 전"
                    },
                    style = Typography().bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Row {
                    Text(
                        text = "${book.totalPages}쪽, 북마크 ${book.bookmarks.size}개",
                        style = Typography().headlineSmall
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = book.lastSeen,
                        style = Typography().headlineSmall
                    )
                }
            }
        }
    }
}


@Composable
fun EmptyBookListComponent(
    onAddClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(100.dp))
        Text(
            text = "아직 학습중인 교재가 없어요!\n 하나를 추가해보시는 건 어때요???",
            style = Typography().headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onAddClicked) {
            Text(text = "추가하러 가기")
        }

    }
}