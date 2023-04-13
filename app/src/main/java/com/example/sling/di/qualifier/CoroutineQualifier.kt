package com.example.sling.di.qualifier

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IODispatcherScope

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcherScope