package com.example.sling.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sling.domain.usecase.ClearEntityUseCase
import com.example.sling.domain.usecase.GetUserBookListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
    localBookListUseCase: GetUserBookListUseCase,
    private val clearEntityUseCase: ClearEntityUseCase
) : ViewModel() {

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val bookList = localBookListUseCase().onStart {
        _isLoading.emit(true)
    }.onEach {
        _isLoading.emit(false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun clearAllEntities() = clearEntityUseCase()
        .onCompletion {
            Log.d("MyTag", it.toString())
        }.launchIn(viewModelScope)
}