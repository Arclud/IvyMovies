package com.ivy.movie.domain

import com.ivy.movie.domain.media.MediaItem
import com.ivy.movie.domain.media.MediaRepository
import com.ivy.movie.domain.media.MediaType
import com.ivy.movie.domain.media.SearchMediaUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchUseCaseTest {

    @Test
    fun `blank query returns popular content`() = runTest {
        val useCase = SearchMediaUseCase(FakeMediaRepository())

        val result = useCase("")

        assertEquals(listOf(movie), result)
    }

    @Test
    fun `english query searches movies and series`() = runTest {
        val useCase = SearchMediaUseCase(FakeMediaRepository())

        val result = useCase("matrix")

        assertEquals(listOf(movie), result)
    }

    @Test
    fun `cyrillic query is passed to repository with russian locale`() = runTest {
        val repository = FakeMediaRepository()
        val useCase = SearchMediaUseCase(repository)

        useCase("матрица")

        assertEquals("ru-RU", repository.lastLanguage)
    }

    @Test
    fun `empty search result returns empty list`() = runTest {
        val useCase = SearchMediaUseCase(FakeMediaRepository(searchResult = emptyList()))

        val result = useCase("missing")

        assertTrue(result.isEmpty())
    }

    private class FakeMediaRepository(
        private val searchResult: List<MediaItem> = listOf(movie),
    ) : MediaRepository {
        var lastLanguage: String? = null

        override suspend fun popular(page: Int, language: String): List<MediaItem> = listOf(movie)

        override suspend fun search(query: String, page: Int, language: String): List<MediaItem> {
            lastLanguage = language
            return searchResult
        }

        override suspend fun details(id: Long, type: MediaType, language: String): MediaItem = movie
    }

    companion object {
        val movie = MediaItem(
            id = 603,
            type = MediaType.Movie,
            title = "The Matrix",
            overview = "A hacker discovers reality.",
            posterUrl = "https://image.tmdb.org/t/p/w342/poster.jpg",
            rating = 8.2,
            releaseDate = "1999-03-31",
        )
    }
}
