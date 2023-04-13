package com.example.sling.data.datasource

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.example.sling.data.database.BookDao
import com.example.sling.data.mapper.toBook
import com.example.sling.data.mapper.toBookEntity
import com.example.sling.domain.model.Book
import com.example.sling.extension.flowMap
import com.example.sling.extension.parseToString
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import java.io.File
import java.time.LocalDate
import javax.inject.Inject

class LocalBookDataSourceImpl @Inject constructor(
    private val bookDao: BookDao,
    private val downloadManager: DownloadManager,
) : LocalBookDataSource {

    override fun getBookList(): Flow<List<Book>> = bookDao.getBookList().flowMap { flowItem ->
        flowItem.toBook()
    }

    override fun getBookById(bookId: String): Flow<Book> = flow {
        val currentBook = bookDao.getBookById(bookId = bookId)
        emit(currentBook.toBook())
    }

    override fun insertBook(bookList: List<Book>): Flow<Boolean> = flow {
        bookDao.addBooks(bookList.map {
            createDownloadLink(it).toBookEntity()
        })
        emit(true)
    }.catch {
        it.printStackTrace()
        emit(false)
    }

    override suspend fun updateStudyTime(time: Int, bookId: String) {
        coroutineScope {
            bookDao.updateBookStudyTime(time, bookId, LocalDate.now().parseToString())
        }
    }

    override fun clearTable(): Flow<Boolean> = callbackFlow {
        val urlList = bookDao.getBookUrls()
        try {
            urlList.forEach {
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "${it.title}.pdf"
                )
                if (file.delete()) {
                    bookDao.clearEntity()
                    close()
                } else {
                    close()
                }
            }
        } catch (e: Exception) {
            close(e)
        }
        awaitClose()
    }


    private suspend fun createDownloadLink(book: Book): Book = coroutineScope {
        Log.d("MyTagLocalDataSource", book.toString())
        val fileUri = Uri.parse(book.url)
        val request = DownloadManager.Request(fileUri)
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "${book.title}.pdf"
        )
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        with(request) {
            setTitle("${book.title}.pdf")
            setDestinationUri(Uri.fromFile(file))
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        }
        downloadManager.enqueue(request).let {
            val uri = Uri.fromFile(file)
            Log.d("MyTagDataSource", uri.toString())
            return@coroutineScope book.copy(
                url = uri.toString()
            )
        }
    }

}
