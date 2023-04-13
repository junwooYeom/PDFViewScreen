package com.example.sling.di.module

import com.example.sling.data.repository.BookRepositoryImpl
import com.example.sling.data.repository.BookmarkRepositoryImpl
import com.example.sling.domain.repository.BookRepository
import com.example.sling.domain.repository.BookmarkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindsBookRepository(impl: BookRepositoryImpl): BookRepository

    @Binds
    @Singleton
    fun bindsBookmarkRepository(impl: BookmarkRepositoryImpl): BookmarkRepository
}