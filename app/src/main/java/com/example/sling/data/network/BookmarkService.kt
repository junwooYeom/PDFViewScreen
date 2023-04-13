package com.example.sling.data.network

import com.example.sling.data.model.BookmarkDto
import kotlinx.coroutines.flow.Flow

interface BookmarkService {

    fun getBookmarkList(): Flow<List<BookmarkDto>>

    fun handleBookmarkPage(bookId: String, page: Int): Flow<Boolean>
}