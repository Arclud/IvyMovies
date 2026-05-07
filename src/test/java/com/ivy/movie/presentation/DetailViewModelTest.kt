package com.ivy.movie.presentation

import com.ivy.movie.domain.favorites.FavoritesRepository
import com.ivy.movie.domain.media.MediaItem
import com.ivy.movie.domain.media.MediaRepository
import com.ivy.movie.domain.media.MediaType
import com.ivy.movie.presentation.detail.DetailEvent
import com.ivy.movie.presentation.detail.DetailViewModel
import com.ivy.movie.presentation.state.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun down() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load details emits success`() = runTest {
        val viewModel = DetailViewModel(FakeMediaRepository(), FakeFavoritesRepository())

        viewModel.onEvent(DetailEvent.Load(id = 603, type = MediaType.Movie))
        advanceUntilIdle()

        assertEquals(ViewState.Success(movie), viewModel.state.value.content)
    }

    @Test
    fun `favorite toggle saves and removes item`() = runTest {
        val favorites = FakeFavoritesRepository()
        val viewModel = DetailViewModel(FakeMediaRepository(), favorites)

        viewModel.onEvent(DetailEvent.Load(id = 603, type = MediaType.Movie))
        advanceUntilIdle()
        viewModel.onEvent(DetailEvent.ToggleFavorite)
        advanceUntilIdle()
        assertEquals(listOf(movie), favorites.items.value)

        viewModel.onEvent(DetailEvent.ToggleFavorite)
        advanceUntilIdle()
        assertEquals(emptyList<MediaItem>(), favorites.items.value)
    }

    private class FakeMediaRepository : MediaRepository {
        override suspend fun popular(page: Int, language: String): List<MediaItem> = listOf(movie)
        override suspend fun search(query: String, page: Int, language: String): List<MediaItem> = listOf(movie)
        override suspend fun details(id: Long, type: MediaType, language: String): MediaItem = movie
    }

    private class FakeFavoritesRepository : FavoritesRepository {
        val items = MutableStateFlow<List<MediaItem>>(emptyList())
        override fun observeFavorites(): Flow<List<MediaItem>> = items
        override suspend fun add(item: MediaItem) { items.value = items.value + item }
        override suspend fun remove(id: Long, type: MediaType) { items.value = items.value.filterNot { it.id == id && it.type == type } }
        override suspend fun isFavorite(id: Long, type: MediaType): Boolean = items.value.any { it.id == id && it.type == type }
    }

    companion object {
        val movie = MediaItem(603, MediaType.Movie, "The Matrix", "A hacker.", "", 8.2, "1999-03-31")
    }
}
