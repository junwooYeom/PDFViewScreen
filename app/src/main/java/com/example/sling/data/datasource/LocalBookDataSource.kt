package com.example.sling.data.datasource

import com.example.sling.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface LocalBookDataSource {

    fun getBookList(): Flow<List<Book>>

    fun getBookById(bookId: String): Flow<Book>

    fun insertBook(bookList: List<Book>): Flow<Boolean>

    suspend fun updateStudyTime(time: Int, bookId: String)

    fun clearTable(): Flow<Boolean>
}