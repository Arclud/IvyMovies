package com.ivy.movie.data.repository

import com.ivy.movie.data.local.FavoriteMediaDao
import com.ivy.movie.data.local.entity.toDomain
import com.ivy.movie.data.local.entity.toEntity
import com.ivy.movie.domain.favorites.FavoritesRepository
import com.ivy.movie.domain.media.MediaItem
import com.ivy.movie.domain.media.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val dao: FavoriteMediaDao,
) : FavoritesRepository {
    override fun observeFavorites(): Flow<List<MediaItem>> {
        return dao.observeAll().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun add(item: MediaItem) {
        dao.upsert(item.toEntity())
    }

    override suspend fun remove(id: Long, type: MediaType) {
        dao.delete(id = id, type = type.apiName)
    }

    override suspend fun isFavorite(id: Long, type: MediaType): Boolean {
        return dao.exists(id = id, type = type.apiName)
    }
}
