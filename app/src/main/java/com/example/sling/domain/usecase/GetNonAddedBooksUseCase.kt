package com.example.sling.domain.usecase

import android.util.Log
import com.example.sling.domain.model.Book
import com.example.sling.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetNonAddedBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {

    operator fun invoke(): Flow<List<Book>> = combine(
        bookRepository.getBooksFromLocal(),
        bookRepository.getBooksFromRemote()
    ) { local, remote ->
        Log.d("MyTag", "UseCase: $remote")
        remote.filter { book -> local.all { it.id != book.id } }
    }
}