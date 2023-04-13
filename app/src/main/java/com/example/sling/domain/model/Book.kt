package com.example.sling.domain.model

data class Book(
    val id: String,
    val title: String,
    val url: String,
    val totalPages: Int,
    val thumbnailUrl: String,
    val time: Int? = null,
    val lastSeen: String,
    val bookmarks: List<Int>,
) {
    val isStudying: Boolean = run {
        time != null && time > 0
    }
}