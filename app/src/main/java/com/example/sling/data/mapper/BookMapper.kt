package com.example.sling.data.mapper

import com.example.sling.data.model.BookDto
import com.example.sling.data.model.BookEntity
import com.example.sling.domain.model.Book
import com.example.sling.extension.parseToString
import java.time.LocalDate

fun BookEntity.toBook(): Book =
    Book(
        id = id,
        url = url,
        title = title,
        totalPages = pages,
        thumbnailUrl = thumbnail,
        time = time,
        lastSeen = lastSeen,
        bookmarks = emptyList()
    )

fun Book.toBookEntity(): BookEntity =
    BookEntity(
        id = id,
        url = url,
        title = title,
        pages = totalPages,
        thumbnail = thumbnailUrl,
        time = time,
        lastSeen = lastSeen
    )

fun BookDto.toBook(): Book =
    Book(
        id = id,
        url = url,
        title = title,
        totalPages = totalPages,
        thumbnailUrl = thumbnailUrl,
        lastSeen = LocalDate.now().parseToString(),
        bookmarks = emptyList()
    )