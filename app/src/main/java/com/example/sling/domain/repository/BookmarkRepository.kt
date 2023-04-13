package com.example.sling.domain.repository

import com.example.sling.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {

    fun getBookList(): Flow<List<Bookmark>>

    fun handleBookmark(bookId: String, bookPage: Int): Flow<Boolean>
}