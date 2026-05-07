package com.ivy.movie.data.repository

import com.ivy.movie.data.remote.TmdbApi
import com.ivy.movie.data.remote.dto.MediaDto
import com.ivy.movie.domain.media.MediaItem
import com.ivy.movie.domain.media.MediaRepository
import com.ivy.movie.domain.media.MediaType
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val api: TmdbApi,
) : MediaRepository {
    override suspend fun popular(page: Int, language: String): List<MediaItem> {
        return api.trending(page = page, language = language)
            .results
            .mapNotNull { it.toDomain() }
    }

    override suspend fun search(query: String, page: Int, language: String): List<MediaItem> {
        return api.searchMulti(query = query, page = page, language = language)
            .results
            .mapNotNull { it.toDomain() }
    }

    override suspend fun details(id: Long, type: MediaType, language: String): MediaItem {
        val dto = when (type) {
            MediaType.Movie -> api.movieDetails(id = id, language = language)
            MediaType.Series -> api.tvDetails(id = id, language = language)
        }
        return dto.toDomain(type) ?: error("Unsupported media details")
    }

    private fun MediaDto.toDomain(fallbackType: MediaType? = null): MediaItem? {
        val domainType = MediaType.fromApi(mediaType) ?: fallbackType ?: return null
        val displayTitle = title ?: name ?: return null
        return MediaItem(
            id = id,
            type = domainType,
            title = displayTitle,
            overview = overview.orEmpty(),
            posterUrl = posterPath?.let { "$IMAGE_BASE$it" }.orEmpty(),
            rating = voteAverage ?: 0.0,
            releaseDate = releaseDate ?: firstAirDate ?: "",
        )
    }

    companion object {
        private const val IMAGE_BASE = "https://image.tmdb.org/t/p/w342"
    }
}
