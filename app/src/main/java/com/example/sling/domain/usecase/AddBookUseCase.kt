package com.example.sling.domain.usecase

import com.example.sling.domain.model.Book
import com.example.sling.domain.repository.BookRepository
import javax.inject.Inject

class AddBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {

    operator fun invoke(list: List<Book>) = bookRepository.addBook(list)
}