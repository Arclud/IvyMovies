package com.ivy.movie.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivy.movie.domain.media.MediaItem
import com.ivy.movie.presentation.components.EmptyView
import com.ivy.movie.presentation.components.ErrorView
import com.ivy.movie.presentation.components.MovieCard
import com.ivy.movie.presentation.components.MovieSearchBar
import com.ivy.movie.presentation.components.ShimmerCard
import com.ivy.movie.presentation.state.ViewState

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenDetails: (MediaItem) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val query by viewModel.query.collectAsState()
    LaunchedEffect(Unit) { viewModel.onEvent(HomeEvent.Load) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Home", style = MaterialTheme.typography.headlineMedium)
        MovieSearchBar(
            query = query,
            onQueryChanged = { viewModel.onEvent(HomeEvent.SearchChanged(it)) },
        )
        when (val content = state) {
            ViewState.Loading -> LoadingList()
            ViewState.Empty -> EmptyView("Nothing found")
            is ViewState.Error -> ErrorView(content.message)
            is ViewState.Success -> MediaList(content.data, onOpenDetails)
        }
    }
}

@Composable
private fun LoadingList() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        repeat(5) { ShimmerCard() }
    }
}

@Composable
private fun MediaList(
    items: List<MediaItem>,
    onOpenDetails: (MediaItem) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 4.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = items,
            key = { "${it.type.apiName}-${it.id}" },
        ) { item ->
            MovieCard(item = item, onClick = onOpenDetails)
        }
    }
}
