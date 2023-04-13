package com.example.sling.domain.usecase

import com.example.sling.domain.repository.BookRepository
import javax.inject.Inject

class ClearEntityUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {

    operator fun invoke() = bookRepository.clearAllBooks()
}