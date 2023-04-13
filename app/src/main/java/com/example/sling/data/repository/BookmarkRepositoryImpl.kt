package com.example.sling.data.repository

import com.example.sling.data.network.BookmarkService
import com.example.sling.data.mapper.toBookmarkList
import com.example.sling.domain.model.Bookmark
import com.example.sling.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkService: BookmarkService
) : BookmarkRepository {

    override fun getBookList(): Flow<List<Bookmark>> = bookmarkService.getBookmarkList().map {
        it.toBookmarkList()
    }

    override fun handleBookmark(bookId: String, bookPage: Int): Flow<Boolean> =
        bookmarkService.handleBookmarkPage(
            bookId, bookPage
        )
}