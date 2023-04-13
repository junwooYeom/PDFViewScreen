package com.example.sling.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.sling.domain.model.Book
import com.example.sling.ui.viewmodel.AddBookListViewModel

@Composable
fun AddBookScreen(
    viewModel: AddBookListViewModel = hiltViewModel(),
    onAllItemInserted: () -> Unit
) {
    val checkedIdList by viewModel.checkedBookList.collectAsState()
    val bookItemList by viewModel.remoteBookList.collectAsState(emptyList())
    val bookInserted by viewModel.bookAllInserted.collectAsState()
    if (bookInserted) {
        onAllItemInserted()
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            columns = GridCells.Adaptive(120.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bookItemList) { book ->
                AddItemComponent(
                    currentBook = book,
                    isChecked = checkedIdList.contains(book.id),
                    onItemCheck = {
                        if (it) {
                            viewModel.checkBook(book.id)
                        } else {
                            viewModel.unCheckBook(book.id)
                        }
                    }
                )
            }
        }

        Button(
            modifier = Modifier.padding(bottom = 20.dp),
            onClick = {
                viewModel.addBooks()
            }) {
            Text(text = "책 다운로드 하기")
        }
    }
}

@Composable
fun AddItemComponent(
    currentBook: Book,
    isChecked: Boolean,
    onItemCheck: (isChecked: Boolean) -> Unit
) {

    Column(
        modifier = Modifier.clickable(
            interactionSource = MutableInteractionSource(),
            indication = null,
            onClick = {
                onItemCheck(isChecked.not())
            }
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            AsyncImage(
                modifier = Modifier
                    .width(120.dp)
                    .height(160.dp),
                model = currentBook.thumbnailUrl,
                contentDescription = "Thumbnail"
            )

            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Check Icon",
                tint = if (isChecked) {
                    Color.Blue
                } else {
                    Color.LightGray
                }
            )
        }

        Text(
            text = currentBook.title,
            overflow = TextOverflow.Ellipsis
        )

    }

}
