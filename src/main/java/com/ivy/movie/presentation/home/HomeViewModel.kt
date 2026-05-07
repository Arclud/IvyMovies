package com.ivy.movie.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.movie.domain.media.MediaItem
import com.ivy.movie.domain.media.SearchMediaUseCase
import com.ivy.movie.presentation.state.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeEvent {
    data object Load : HomeEvent
    data class SearchChanged(val query: String) : HomeEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchMedia: SearchMediaUseCase,
) : ViewModel() {
    private val mutableState = MutableStateFlow<ViewState<List<MediaItem>>>(ViewState.Loading)
    val state: StateFlow<ViewState<List<MediaItem>>> = mutableState.asStateFlow()

    private val mutableQuery = MutableStateFlow("")
    val query: StateFlow<String> = mutableQuery.asStateFlow()

    private var searchJob: Job? = null
    private var hasLoadedInitialContent = false

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Load -> {
                if (!hasLoadedInitialContent) {
                    hasLoadedInitialContent = true
                    load(query.value, debounce = false)
                }
            }
            is HomeEvent.SearchChanged -> {
                mutableQuery.value = event.query
                load(event.query, debounce = true)
            }
        }
    }

    private fun load(query: String, debounce: Boolean) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (!debounce || mutableState.value !is ViewState.Success) {
                mutableState.value = ViewState.Loading
            }
            if (debounce) delay(350)
            // TODO Тут можно было бы использовать свои custom ошибки
            runCatching { searchMedia(query) }
                .onSuccess { items ->
                    mutableState.value = if (items.isEmpty()) ViewState.Empty else ViewState.Success(items)
                }
                .onFailure { error ->
                    mutableState.value = ViewState.Error(error.message ?: "Unknown error")
                }
        }
    }
}
