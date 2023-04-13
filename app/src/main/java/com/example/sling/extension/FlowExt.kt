package com.example.sling.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


fun <T, R> Flow<List<T>>.flowMap(transform: (T) -> R): Flow<List<R>> =
    map { list ->
        list.map(transform)
    }

