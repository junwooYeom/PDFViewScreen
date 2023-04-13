package com.example.sling.domain.usecase

import com.example.sling.domain.repository.BookRepository
import com.example.sling.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetUserBookListUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val bookmarkRepository: BookmarkRepository
) {

    operator fun invoke() = combine(bookRepository.getBooksFromLocal(), bookmarkRepository.getBookList()) { userList, bookmarkList ->
        userList.map { book ->
            book.copy(
                bookmarks = bookmarkList.find { it.pdfId == book.id }?.index ?: emptyList()
            )
        }
    }
}