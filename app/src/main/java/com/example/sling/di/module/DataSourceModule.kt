package com.example.sling.di.module

import com.example.sling.data.datasource.LocalBookDataSource
import com.example.sling.data.datasource.LocalBookDataSourceImpl
import com.example.sling.data.datasource.RemoteBookDataSource
import com.example.sling.data.datasource.RemoteBookDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {

    // LocalDataSource
    @Binds
    @Singleton
    fun bindsLocalBookDataSource(impl: LocalBookDataSourceImpl): LocalBookDataSource


    // RemoteDataSource
    @Binds
    @Singleton
    fun bindsRemoteBookDataSource(impl: RemoteBookDataSourceImpl): RemoteBookDataSource
}