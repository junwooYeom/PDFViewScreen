package com.example.sling.domain.usecase

import com.example.sling.domain.repository.BookmarkRepository
import javax.inject.Inject

class AddBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
) {

    operator fun invoke(pdfId: String, pageIndex: Int) = bookmarkRepository.handleBookmark(
        pdfId, pageIndex
    )
}