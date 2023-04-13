package com.example.sling.domain.usecase

import com.example.sling.domain.model.Book
import com.example.sling.domain.repository.BookRepository
import com.example.sling.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetBookByIdUseCase @Inject constructor(
    private val repository: BookRepository,
    private val bookmarkRepository: BookmarkRepository,
) {
    operator fun invoke(bookId: String): Flow<Book> = combine(
        repository.getBookFromLocalById(bookId = bookId),
        bookmarkRepository.getBookList()
    ) { book, bookmarks ->
        val currentBookmark = bookmarks.find { it.pdfId == book.id }?.index ?: emptyList()
        book.copy(
            bookmarks = currentBookmark
        )
    }
}