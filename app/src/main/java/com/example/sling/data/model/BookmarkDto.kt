package com.example.sling.data.model

@kotlinx.serialization.Serializable
data class BookmarkDto(
    val createdAt: String,
    val pdfId: String,
    val page: Int,
    val userId: String,
    val id: String,
)