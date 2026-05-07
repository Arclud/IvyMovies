package com.ivy.movie.presentation.favorites

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
import com.ivy.movie.presentation.components.ShimmerCard
import com.ivy.movie.presentation.state.ViewState

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onOpenDetails: (MediaItem) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.onEvent(FavoritesEvent.Load) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Saved", style = MaterialTheme.typography.headlineMedium)
        when (val content = state) {
            ViewState.Loading -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(3) { ShimmerCard() }
            }
            ViewState.Empty -> EmptyView("No saved movies or series yet")
            is ViewState.Error -> ErrorView(content.message)
            is ViewState.Success -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 4.dp, bottom = 112.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(content.data, key = { "${it.type.apiName}-${it.id}" }) { item ->
                    MovieCard(item = item, onClick = onOpenDetails)
                }
            }
        }
    }
}
