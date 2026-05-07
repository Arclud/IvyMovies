package com.ivy.movie.domain.favorites

import com.ivy.movie.domain.media.MediaItem
import com.ivy.movie.domain.media.MediaType
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeFavorites(): Flow<List<MediaItem>>
    suspend fun add(item: MediaItem)
    suspend fun remove(id: Long, type: MediaType)
    suspend fun isFavorite(id: Long, type: MediaType): Boolean
}
