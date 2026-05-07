package com.ivy.movie.data

import com.ivy.movie.data.remote.TmdbApi
import com.ivy.movie.data.remote.dto.MediaDto
import com.ivy.movie.data.remote.dto.MediaListDto
import com.ivy.movie.data.repository.MediaRepositoryImpl
import com.ivy.movie.domain.media.MediaType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaRepositoryImplTest {

    @Test
    fun `popular maps movie and tv dto to domain`() = runTest {
        val repository = MediaRepositoryImpl(FakeTmdbApi())

        val result = repository.popular(page = 1, language = "en-US")

        assertEquals(listOf("The Matrix", "Dark"), result.map { it.title })
        assertEquals(listOf(MediaType.Movie, MediaType.Series), result.map { it.type })
    }

    @Test
    fun `search returns empty list when api has no results`() = runTest {
        val repository = MediaRepositoryImpl(FakeTmdbApi(search = MediaListDto(emptyList())))

        val result = repository.search(query = "none", page = 1, language = "en-US")

        assertTrue(result.isEmpty())
    }

    private class FakeTmdbApi(
        private val search: MediaListDto = MediaListDto(emptyList()),
        private val fail: Boolean = false,
    ) : TmdbApi {
        override suspend fun trending(page: Int, language: String): MediaListDto {
            if (fail) error("network")
            return MediaListDto(
                results = listOf(
                    MediaDto(603, "movie", "The Matrix", null, "A hacker.", "/poster.jpg", 8.2, "1999-03-31", null),
                    MediaDto(70523, "tv", null, "Dark", "A missing child.", "/dark.jpg", 8.4, null, "2017-12-01"),
                )
            )
        }

        override suspend fun searchMulti(query: String, page: Int, language: String): MediaListDto = search

        override suspend fun movieDetails(id: Long, language: String): MediaDto =
            MediaDto(id, "movie", "The Matrix", null, "A hacker.", "/poster.jpg", 8.2, "1999-03-31", null)

        override suspend fun tvDetails(id: Long, language: String): MediaDto =
            MediaDto(id, "tv", null, "Dark", "A missing child.", "/dark.jpg", 8.4, null, "2017-12-01")
    }
}
