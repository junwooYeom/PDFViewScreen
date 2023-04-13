package com.example.sling.domain.usecase

import com.example.sling.domain.repository.BookRepository
import javax.inject.Inject

class UpdateStudyTimeUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {

    suspend operator fun invoke(bookId: String, pageTime: Int) = bookRepository.updateCurrentBookStudyTime(
        bookId = bookId, pageTime
    )

}