package com.ivy.movie.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.movie.domain.favorites.FavoritesRepository
import com.ivy.movie.domain.media.MediaItem
import com.ivy.movie.domain.media.MediaRepository
import com.ivy.movie.domain.media.MediaType
import com.ivy.movie.presentation.state.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailState(
    val content: ViewState<MediaItem> = ViewState.Loading,
    val isFavorite: Boolean = false,
)

sealed interface DetailEvent {
    data class Load(val id: Long, val type: MediaType) : DetailEvent
    data object ToggleFavorite : DetailEvent
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {
    private val mutableState = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = mutableState.asStateFlow()

    fun onEvent(event: DetailEvent) {
        when (event) {
            is DetailEvent.Load -> load(event.id, event.type)
            DetailEvent.ToggleFavorite -> toggleFavorite()
        }
    }

    private fun load(id: Long, type: MediaType) {
        viewModelScope.launch {
            mutableState.value = DetailState(content = ViewState.Loading)
            // TODO Тут можно было бы использовать свои custom ошибки
            runCatching { mediaRepository.details(id = id, type = type) }
                .onSuccess { item ->
                    mutableState.value = DetailState(
                        content = ViewState.Success(item),
                        isFavorite = favoritesRepository.isFavorite(item.id, item.type),
                    )
                }
                .onFailure { error ->
                    mutableState.value = DetailState(
                        content = ViewState.Error(error.message ?: "Unknown error")
                    )
                }
        }
    }

    private fun toggleFavorite() {
        val item = (state.value.content as? ViewState.Success)?.data ?: return
        viewModelScope.launch {
            if (state.value.isFavorite) {
                favoritesRepository.remove(item.id, item.type)
            } else {
                favoritesRepository.add(item)
            }
            mutableState.update { it.copy(isFavorite = !it.isFavorite) }
        }
    }
}
