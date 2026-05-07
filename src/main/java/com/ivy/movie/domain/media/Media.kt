package com.ivy.movie.domain.media

import javax.inject.Inject

enum class MediaType(val apiName: String) {
    Movie("movie"),
    Series("tv");

    companion object {
        fun fromApi(value: String?): MediaType? = when (value) {
            Movie.apiName -> Movie
            Series.apiName -> Series
            else -> null
        }
    }
}

data class MediaItem(
    val id: Long,
    val type: MediaType,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val rating: Double,
    val releaseDate: String,
)

interface MediaRepository {
    suspend fun popular(page: Int = 1, language: String = "en-US"): List<MediaItem>
    suspend fun search(query: String, page: Int = 1, language: String = "en-US"): List<MediaItem>
    suspend fun details(id: Long, type: MediaType, language: String = "en-US"): MediaItem
}

class SearchMediaUseCase @Inject constructor(
    private val repository: MediaRepository,
) {
    suspend operator fun invoke(query: String, page: Int = 1): List<MediaItem> {
        val normalizedQuery = query.trim()
        val language = if (normalizedQuery.hasCyrillic()) "ru-RU" else "en-US"
        return if (normalizedQuery.isBlank()) {
            repository.popular(page = page, language = language)
        } else {
            repository.search(query = normalizedQuery, page = page, language = language)
        }
    }

    private fun String.hasCyrillic(): Boolean = any { it in '\u0400'..'\u04FF' }
}
