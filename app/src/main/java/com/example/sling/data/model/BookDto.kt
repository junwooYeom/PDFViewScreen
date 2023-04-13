package com.example.sling.data.model

data class BookDto(
    val id: String,
    val thumbnailUrl: String,
    val title: String,
    val totalPages: Int,
    val url: String,
)