package com.example.sling.di.module

import com.example.sling.data.network.BookmarkService
import com.example.sling.data.network.BookmarkServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ServiceModule {
    @Binds
    @Singleton
    fun bindsBookmarkService(impl: BookmarkServiceImpl): BookmarkService
}