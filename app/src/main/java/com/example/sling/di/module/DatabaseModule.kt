package com.example.sling.di.module

import android.content.Context
import androidx.room.Room
import com.example.sling.data.database.BookDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesBookDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            BookDatabase::class.java,
            name = "bookData_db"
        ).build()

    @Provides
    @Singleton
    fun providesBookDao(database: BookDatabase) = database.bookDao()
}