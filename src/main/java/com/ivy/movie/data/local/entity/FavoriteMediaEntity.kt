package com.ivy.movie.data.local.entity

import androidx.room.Entity
import com.ivy.movie.domain.media.MediaItem
import com.ivy.movie.domain.media.MediaType

@Entity(
    tableName = "favorite_media",
    primaryKeys = ["id", "type"],
)
data class FavoriteMediaEntity(
    val id: Long,
    val type: String,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val rating: Double,
    val releaseDate: String,
    val cachedAt: Long = 0L,
)

fun FavoriteMediaEntity.toDomain(): MediaItem = MediaItem(
    id = id,
    type = if (type == MediaType.Series.apiName) MediaType.Series else MediaType.Movie,
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    rating = rating,
    releaseDate = releaseDate,
)

fun MediaItem.toEntity(cachedAt: Long = System.currentTimeMillis()): FavoriteMediaEntity =
    FavoriteMediaEntity(
        id = id,
        type = type.apiName,
        title = title,
        overview = overview,
        posterUrl = posterUrl,
        rating = rating,
        releaseDate = releaseDate,
        cachedAt = cachedAt,
    )
