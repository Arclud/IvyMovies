package com.ivy.movie.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.ivy.movie.domain.media.MediaItem
import com.ivy.movie.presentation.TestTags
import com.ivy.movie.presentation.testTagAndContentDescription

@Composable
fun MovieSearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier
            .fillMaxWidth()
            .testTagAndContentDescription(TestTags.HomeSearchInput),
        singleLine = true,
        placeholder = { Text("Search movies and series") },
    )
}

@Composable
fun MovieCard(
    item: MediaItem,
    onClick: (MediaItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTagAndContentDescription(TestTags.movieCard(item.id, item.type.apiName))
            .clickable { onClick(item) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PosterImage(item.posterUrl, Modifier.size(width = 76.dp, height = 112.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "${item.type.name.lowercase().replaceFirstChar { it.uppercase() }} • ${item.releaseDate}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = item.overview,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
                RatingBadge(item.rating)
            }
        }
    }
}

@Composable
fun PosterImage(url: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (url.isBlank()) {
            Text("Poster", style = MaterialTheme.typography.labelMedium)
        } else {
            SubcomposeAsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
            ) {
                when (painter.state) {
                    is coil.compose.AsyncImagePainter.State.Success -> {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(220)),
                            exit = fadeOut(animationSpec = tween(120)),
                        ) {
                            SubcomposeAsyncImageContent()
                        }
                    }
                    is coil.compose.AsyncImagePainter.State.Error -> {
                        Text("Poster", style = MaterialTheme.typography.labelMedium)
                    }
                    else -> PosterImageShimmer()
                }
            }
        }
    }
}

@Composable
private fun PosterImageShimmer() {
    val transition = rememberInfiniteTransition(label = "posterShimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.24f,
        targetValue = 0.72f,
        animationSpec = infiniteRepeatable(tween(760), RepeatMode.Reverse),
        label = "posterAlpha",
    )
    val brush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha),
            MaterialTheme.colorScheme.surface.copy(alpha = alpha),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha),
        )
    )
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
    )
}

@Composable
fun RatingBadge(rating: Double, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.16f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text("*", color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(14.dp))
        Text(String.format("%.1f", rating), style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun ShimmerCard(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "alpha",
    )
    val brush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha),
            MaterialTheme.colorScheme.surface.copy(alpha = alpha),
        )
    )
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTagAndContentDescription(TestTags.ShimmerCard),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Spacer(Modifier.size(width = 76.dp, height = 112.dp).clip(RoundedCornerShape(8.dp)).background(brush))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Spacer(Modifier.fillMaxWidth(0.7f).height(18.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                Spacer(Modifier.fillMaxWidth().height(14.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                Spacer(Modifier.fillMaxWidth(0.85f).height(14.dp).clip(RoundedCornerShape(4.dp)).background(brush))
            }
        }
    }
}

@Composable
fun ErrorView(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Error", color = MaterialTheme.colorScheme.error)
        Text(message, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun EmptyView(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
