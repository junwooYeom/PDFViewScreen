package com.example.sling.data.repository

import com.example.sling.data.datasource.LocalBookDataSource
import com.example.sling.data.datasource.RemoteBookDataSource
import com.example.sling.domain.model.Book
import com.example.sling.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val localBookDataSource: LocalBookDataSource,
    private val remoteBookDataSource: RemoteBookDataSource
) : BookRepository {
    override fun getBooksFromLocal(): Flow<List<Book>> = localBookDataSource.getBookList()

    override fun getBookFromLocalById(bookId: String): Flow<Book> =
        localBookDataSource.getBookById(bookId = bookId)

    override fun getBooksFromRemote(): Flow<List<Book>> = remoteBookDataSource.getItemList()

    override suspend fun updateCurrentBookStudyTime(bookId: String, time: Int) =
        localBookDataSource.updateStudyTime(time, bookId)

    override fun addBook(bookList: List<Book>): Flow<Boolean> =
        localBookDataSource.insertBook(bookList = bookList)

    override fun clearAllBooks(): Flow<Boolean> = localBookDataSource.clearTable()

}