package com.ivy.movie.presentation.state

sealed interface ViewState<out T> {
    data object Loading : ViewState<Nothing>
    data class Success<T>(val data: T) : ViewState<T>
    data object Empty : ViewState<Nothing>
    data class Error(val message: String) : ViewState<Nothing>
}
