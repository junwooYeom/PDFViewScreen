package com.example.sling.di.module

import com.example.sling.di.qualifier.DefaultDispatcherScope
import com.example.sling.di.qualifier.IODispatcherScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopeModule {

    @Singleton
    @Provides
    @IODispatcherScope
    fun providesIOCoroutineScope(): CoroutineScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    @Singleton
    @Provides
    @DefaultDispatcherScope
    fun providesDefaultCoroutineScope(): CoroutineScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default
    )
}