package com.example.sling.data.datasource

import com.example.sling.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface RemoteBookDataSource {

    fun getItemList(): Flow<List<Book>>
}