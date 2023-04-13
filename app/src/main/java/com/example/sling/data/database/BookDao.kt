package com.example.sling.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sling.data.model.BookEntity
import com.example.sling.data.model.BookTuple
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM BookEntity ORDER BY time DESC")
    fun getBookList(): Flow<List<BookEntity>>

    @Query("SELECT url, title FROM BookEntity")
    suspend fun getBookUrls(): List<BookTuple>

    @Query("SELECT * FROM BookEntity WHERE id=:bookId")
    suspend fun getBookById(bookId: String): BookEntity

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addBooks(bookEntity: List<BookEntity>)

    @Query("UPDATE bookEntity SET time=:time,lastSeen=:lastSeen WHERE id=:bookId")
    suspend fun updateBookStudyTime(time: Int, bookId: String, lastSeen: String)

    @Query("DELETE FROM BookEntity")
    suspend fun clearEntity()
}