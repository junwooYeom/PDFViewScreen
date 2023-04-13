package com.example.sling.data.datasource

import com.example.sling.data.mapper.toBook
import com.example.sling.data.model.BookDto
import com.example.sling.di.qualifier.IODispatcherScope
import com.example.sling.domain.model.Book
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class RemoteBookDataSourceImpl @Inject constructor(
    @IODispatcherScope private val coroutineScope: CoroutineScope
) : RemoteBookDataSource {

    private val database = Firebase.database.reference
    override fun getItemList(): Flow<List<Book>> = callbackFlow {
        coroutineScope.launch {
            database.get().addOnSuccessListener {
                val bookList = mutableListOf<BookDto>()
                it.child("Books").children.forEach { dataSnapshot ->
                    val currentBook = BookDto(
                        id = dataSnapshot.child("id").value.toString(),
                        title = dataSnapshot.child("title").value.toString(),
                        thumbnailUrl = dataSnapshot.child("thumbnailUrl").value.toString(),
                        url = dataSnapshot.child("url").value.toString(),
                        totalPages = dataSnapshot.child("totalPages").getValue(Int::class.java) ?: 0
                    )
                    bookList.add(currentBook)
                }
                trySend(bookList.map { bookDto -> bookDto.toBook() }).also {
                    close()
                }
            }.addOnFailureListener {
                close(it)
            }.addOnCanceledListener {
                close(Throwable("CANCELLED"))
            }
        }
        awaitClose()
    }
}
