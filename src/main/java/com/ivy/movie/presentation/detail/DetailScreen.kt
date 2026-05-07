package com.ivy.movie.presentation.detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivy.movie.R
import com.ivy.movie.domain.media.MediaType
import com.ivy.movie.presentation.components.ErrorView
import com.ivy.movie.presentation.components.PosterImage
import com.ivy.movie.presentation.components.RatingBadge
import com.ivy.movie.presentation.components.ShimmerCard
import com.ivy.movie.presentation.state.ViewState

@Composable
fun DetailScreen(
    id: Long,
    type: MediaType,
    viewModel: DetailViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(id, type) { viewModel.onEvent(DetailEvent.Load(id, type)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = "Back",
            )
        }
        when (val content = state.content) {
            ViewState.Loading -> repeat(4) { ShimmerCard() }
            ViewState.Empty -> Unit
            is ViewState.Error -> ErrorView(content.message)
            is ViewState.Success -> {
                val item = content.data
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    PosterImage(item.posterUrl, Modifier.size(width = 132.dp, height = 196.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(item.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(item.type.name.lowercase().replaceFirstChar { it.uppercase() })
                        Text(item.releaseDate, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        RatingBadge(item.rating)
                    }
                }
                Text(item.overview, style = MaterialTheme.typography.bodyLarge)
                val favoriteColor by animateColorAsState(
                    targetValue = if (state.isFavorite) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    },
                    label = "favoriteColor",
                )
                val favoriteScale = remember { Animatable(1f) }
                LaunchedEffect(state.isFavorite) {
                    favoriteScale.snapTo(1f)
                    favoriteScale.animateTo(
                        targetValue = 1.06f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium,
                        ),
                    )
                    favoriteScale.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium,
                        ),
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(favoriteScale.value),
                    onClick = { viewModel.onEvent(DetailEvent.ToggleFavorite) },
                    colors = if (state.isFavorite) {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    } else {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    },
                ) {
                    Text(
                        text = if (state.isFavorite) "Remove" else "Save",
                        color = favoriteColor,
                    )
                }
            }
        }
    }
}
