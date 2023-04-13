package com.example.sling.data.network

import android.util.Log
import com.example.sling.data.model.BookmarkDto
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BookmarkServiceImpl @Inject constructor(
    private val httpClient: HttpClient
) : BookmarkService {

    private suspend fun getBookmarkIndexList(): List<BookmarkDto> = httpClient.get("/bookmarks") {
        body = mapOf<String, Any>(
            "userId" to "junwooYeom",
        )
    }

    private suspend fun handleBookmarkIndex(bookId: String, pageIndex: Int): HttpResponse =
        httpClient.post("/bookmarks") {
            body = mapOf(
                "userId" to "junwooYeom",
                "pdfId" to bookId,
                "page" to "$pageIndex"
            )
        }

    override fun getBookmarkList(): Flow<List<BookmarkDto>> = flow {
        val response = getBookmarkIndexList()
        Log.d("MyTag", response.toString())
        emit(response)
    }


    override fun handleBookmarkPage(bookId: String, page: Int): Flow<Boolean> = flow {
        handleBookmarkIndex(bookId, page).status.isSuccess().let {
            emit(it)
        }.also {
            getBookmarkList()
        }
    }

}