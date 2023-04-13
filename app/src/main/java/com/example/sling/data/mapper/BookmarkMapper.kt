package com.example.sling.data.mapper

import com.example.sling.data.model.BookmarkDto
import com.example.sling.domain.model.Bookmark

fun List<BookmarkDto>.toBookmarkList(): List<Bookmark> {
    val currentBookmarkList = mutableListOf<Bookmark>()
    groupBy { it.pdfId }.forEach {(pdfId, values) ->
        val indexList = mutableListOf<Int>()
        values.forEach {
            indexList.add(it.page)
        }
        currentBookmarkList.add(Bookmark(pdfId, indexList))
    }
    return currentBookmarkList
}