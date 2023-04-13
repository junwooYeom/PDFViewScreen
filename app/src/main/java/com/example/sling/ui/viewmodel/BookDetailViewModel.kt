package com.example.sling.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sling.domain.usecase.AddBookmarkUseCase
import com.example.sling.domain.model.Book
import com.example.sling.domain.usecase.GetBookByIdUseCase
import com.example.sling.domain.usecase.UpdateStudyTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val addBookmarkUseCase: AddBookmarkUseCase,
    private val updateStudyTimeUseCase: UpdateStudyTimeUseCase,
) : ViewModel() {

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentBook: MutableStateFlow<Book?> = MutableStateFlow(null)
    val currentBook: StateFlow<Book?> = _currentBook

    private val _currentTime: MutableStateFlow<Int> = MutableStateFlow(0)
    val currentTime: StateFlow<Int> = _currentTime

    fun updateStudyTime(bookId: String, time: Int) {
        viewModelScope.launch {
            currentBook.value?.let {
                updateStudyTimeUseCase(bookId, time)
            }
        }
    }

    fun getBookById(bookId: String) = getBookByIdUseCase(bookId = bookId)
        .onStart { _isLoading.emit(true) }
        .onEach {
            _currentBook.emit(it)
            _currentTime.emit(it.time ?: 0)
        }
        .onCompletion { _isLoading.emit(false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            null,
        ).launchIn(viewModelScope)

    fun bookmarkItem(bookId: String, pageIndex: Int) = addBookmarkUseCase(
        bookId, pageIndex
    ).onCompletion {
        currentBook.value?.let {
            _currentBook.emit(
                it.copy(
                    bookmarks = it.bookmarks.toMutableList().run {
                        add(pageIndex)
                        toList()
                    }
                )
            )
        }
    }.launchIn(viewModelScope)

    fun tickTime(time: Int) = viewModelScope.launch {
        _currentTime.emit(time)
    }
}