package com.example.sling.domain.repository

import com.example.sling.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    fun getBooksFromLocal(): Flow<List<Book>>

    fun getBookFromLocalById(bookId: String): Flow<Book>

    fun getBooksFromRemote(): Flow<List<Book>>

    suspend fun updateCurrentBookStudyTime(bookId: String, time: Int)

    fun addBook(bookList: List<Book>): Flow<Boolean>

    fun clearAllBooks(): Flow<Boolean>
}