package com.example.sling.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sling.domain.usecase.AddBookUseCase
import com.example.sling.domain.usecase.GetNonAddedBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBookListViewModel @Inject constructor(
    getRemoteBookListUseCase: GetNonAddedBooksUseCase,
    private val addBookUseCase: AddBookUseCase,
) : ViewModel() {
    private val _bookAllInserted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val bookAllInserted: StateFlow<Boolean> = _bookAllInserted

    private val _checkedBookList: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val checkedBookList: StateFlow<List<String>> = _checkedBookList

    val remoteBookList = getRemoteBookListUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    fun checkBook(id: String) = viewModelScope.launch {
        _checkedBookList.emit(
            checkedBookList.value.toMutableList().apply {
                add(id)
            }
        )
    }

    fun unCheckBook(id: String) = viewModelScope.launch {
        _checkedBookList.emit(
            checkedBookList.value.toMutableList().apply {
                remove(id)
            }
        )
    }

    fun addBooks() {
        val checkedBookList =
            remoteBookList.value.filter { book -> checkedBookList.value.any { check -> book.id == check } }
        addBookUseCase(
            checkedBookList
        ).onEach {
            _bookAllInserted.emit(it)
        }.launchIn(viewModelScope)

    }
}