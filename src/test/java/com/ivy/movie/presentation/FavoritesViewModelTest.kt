package com.ivy.movie.presentation

import com.ivy.movie.domain.favorites.FavoritesRepository
import com.ivy.movie.domain.media.MediaItem
import com.ivy.movie.domain.media.MediaType
import com.ivy.movie.presentation.favorites.FavoritesEvent
import com.ivy.movie.presentation.favorites.FavoritesViewModel
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
class FavoritesViewModelTest {

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
    fun `empty favorites emits empty state`() = runTest {
        val viewModel = FavoritesViewModel(FakeFavoritesRepository())

        viewModel.onEvent(FavoritesEvent.Load)
        advanceUntilIdle()

        assertEquals(ViewState.Empty, viewModel.state.value)
    }

    @Test
    fun `saved favorites emit success state`() = runTest {
        val viewModel = FavoritesViewModel(FakeFavoritesRepository(listOf(movie)))

        viewModel.onEvent(FavoritesEvent.Load)
        advanceUntilIdle()

        assertEquals(ViewState.Success(listOf(movie)), viewModel.state.value)
    }

    private class FakeFavoritesRepository(
        initial: List<MediaItem> = emptyList(),
    ) : FavoritesRepository {
        private val items = MutableStateFlow(initial)
        override fun observeFavorites(): Flow<List<MediaItem>> = items
        override suspend fun add(item: MediaItem) { items.value = items.value + item }
        override suspend fun remove(id: Long, type: MediaType) { items.value = items.value.filterNot { it.id == id && it.type == type } }
        override suspend fun isFavorite(id: Long, type: MediaType): Boolean = items.value.any { it.id == id && it.type == type }
    }

    companion object {
        val movie = MediaItem(603, MediaType.Movie, "The Matrix", "A hacker.", "", 8.2, "1999-03-31")
    }
}
