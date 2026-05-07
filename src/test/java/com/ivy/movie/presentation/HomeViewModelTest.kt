package com.ivy.movie.presentation

import com.ivy.movie.domain.media.MediaItem
import com.ivy.movie.domain.media.MediaRepository
import com.ivy.movie.domain.media.MediaType
import com.ivy.movie.domain.media.SearchMediaUseCase
import com.ivy.movie.presentation.home.HomeEvent
import com.ivy.movie.presentation.home.HomeViewModel
import com.ivy.movie.presentation.state.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class HomeViewModelTest {

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
    fun `load emits loading then success`() = runTest {
        val viewModel = HomeViewModel(SearchMediaUseCase(FakeMediaRepository()))

        viewModel.onEvent(HomeEvent.Load)
        assertEquals(ViewState.Loading, viewModel.state.value)
        advanceUntilIdle()

        assertEquals(ViewState.Success(listOf(movie)), viewModel.state.value)
    }

    @Test
    fun `load emits error on api failure`() = runTest {
        val viewModel = HomeViewModel(SearchMediaUseCase(FakeMediaRepository(fail = true)))

        viewModel.onEvent(HomeEvent.Load)
        advanceUntilIdle()

        assertEquals(ViewState.Error("network"), viewModel.state.value)
    }

    @Test
    fun `search emits empty state for empty result`() = runTest {
        val viewModel = HomeViewModel(SearchMediaUseCase(FakeMediaRepository(items = emptyList())))

        viewModel.onEvent(HomeEvent.SearchChanged("missing"))
        advanceUntilIdle()

        assertEquals(ViewState.Empty, viewModel.state.value)
    }

    private class FakeMediaRepository(
        private val items: List<MediaItem> = listOf(movie),
        private val fail: Boolean = false,
    ) : MediaRepository {
        override suspend fun popular(page: Int, language: String): List<MediaItem> {
            if (fail) error("network")
            return items
        }

        override suspend fun search(query: String, page: Int, language: String): List<MediaItem> {
            if (fail) error("network")
            return items
        }

        override suspend fun details(id: Long, type: MediaType, language: String): MediaItem = movie
    }

    companion object {
        val movie = MediaItem(603, MediaType.Movie, "The Matrix", "A hacker.", "", 8.2, "1999-03-31")
    }
}
