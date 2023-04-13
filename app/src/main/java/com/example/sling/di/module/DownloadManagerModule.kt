package com.example.sling.di.module

import android.app.DownloadManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DownloadManagerModule {

    @Provides
    fun providesDownloadManager(
        @ApplicationContext context: Context
    ): DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

}