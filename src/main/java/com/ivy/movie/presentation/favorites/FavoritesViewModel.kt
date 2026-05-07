package com.ivy.movie.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.movie.domain.favorites.FavoritesRepository
import com.ivy.movie.domain.media.MediaItem
import com.ivy.movie.presentation.state.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FavoritesEvent {
    data object Load : FavoritesEvent
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {
    private val mutableState = MutableStateFlow<ViewState<List<MediaItem>>>(ViewState.Loading)
    val state: StateFlow<ViewState<List<MediaItem>>> = mutableState.asStateFlow()
    private var observeJob: Job? = null

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            FavoritesEvent.Load -> load()
        }
    }

    private fun load() {
        if (observeJob != null) return
        observeJob = viewModelScope.launch {
            favoritesRepository.observeFavorites().collect { items ->
                mutableState.value = if (items.isEmpty()) ViewState.Empty else ViewState.Success(items)
            }
        }
    }
}
