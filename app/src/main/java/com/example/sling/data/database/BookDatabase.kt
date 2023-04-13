package com.example.sling.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sling.data.model.BookEntity

@Database(
    entities = [
        BookEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BookDatabase : RoomDatabase(){

    abstract fun bookDao(): BookDao
}