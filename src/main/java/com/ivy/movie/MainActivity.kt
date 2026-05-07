package com.ivy.movie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigationevent.NavigationEventDispatcher
import androidx.navigationevent.NavigationEventDispatcherOwner
import androidx.navigationevent.setViewTreeNavigationEventDispatcherOwner
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import com.ivy.movie.domain.media.MediaItem
import com.ivy.movie.domain.media.MediaType
import com.ivy.movie.presentation.detail.DetailScreen
import com.ivy.movie.presentation.detail.DetailViewModel
import com.ivy.movie.presentation.favorites.FavoritesScreen
import com.ivy.movie.presentation.favorites.FavoritesViewModel
import com.ivy.movie.presentation.home.HomeScreen
import com.ivy.movie.presentation.home.HomeViewModel
import com.ivy.movie.presentation.login.LoginScreen
import com.ivy.movie.presentation.login.LoginViewModel
import com.ivy.movie.ui.MovieTheme
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity(), NavigationEventDispatcherOwner {
    override val navigationEventDispatcher = NavigationEventDispatcher {
        onBackPressedDispatcher.onBackPressed()
    }
    private val loginViewModel: LoginViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val favoritesViewModel: FavoritesViewModel by viewModels()
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.setViewTreeNavigationEventDispatcherOwner(this)
        setContent {
            MovieTheme {
                MovieApp(
                    loginViewModel = loginViewModel,
                    homeViewModel = homeViewModel,
                    favoritesViewModel = favoritesViewModel,
                    detailViewModel = detailViewModel,
                )
            }
        }
    }
}

@Serializable
private sealed interface MovieRoute : NavKey {
    @Serializable
    data object Login : MovieRoute

    @Serializable
    data object Main : MovieRoute

    @Serializable
    data class Detail(val id: Long, val type: MediaType) : MovieRoute
}

private enum class MainTab { Home, Saved }

@Composable
private fun MovieApp(
    loginViewModel: LoginViewModel,
    homeViewModel: HomeViewModel,
    favoritesViewModel: FavoritesViewModel,
    detailViewModel: DetailViewModel,
) {
    val backStack = rememberNavBackStack(MovieRoute.Login)

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<MovieRoute.Login> {
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoggedIn = {
                        backStack.clear()
                        backStack.add(MovieRoute.Main)
                    },
                )
            }
            entry<MovieRoute.Main> {
                MainGraph(
                    homeViewModel = homeViewModel,
                    favoritesViewModel = favoritesViewModel,
                    onOpenDetails = { item -> backStack.add(MovieRoute.Detail(item.id, item.type)) },
                )
            }
            entry<MovieRoute.Detail> { route ->
                DetailScreen(
                    id = route.id,
                    type = route.type,
                    viewModel = detailViewModel,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}

@Composable
private fun MainGraph(
    homeViewModel: HomeViewModel,
    favoritesViewModel: FavoritesViewModel,
    onOpenDetails: (MediaItem) -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Home) }
    val itemColors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == MainTab.Home,
                    onClick = { selectedTab = MainTab.Home },
                    icon = { Text("H") },
                    label = { Text("Home") },
                    colors = itemColors,
                )
                NavigationBarItem(
                    selected = selectedTab == MainTab.Saved,
                    onClick = { selectedTab = MainTab.Saved },
                    icon = { Text("S") },
                    label = { Text("Saved") },
                    colors = itemColors,
                )
            }
        },
    ) { padding ->
        androidx.compose.foundation.layout.Box(Modifier.padding(padding)) {
            Crossfade(
                targetState = selectedTab,
                animationSpec = tween(durationMillis = 220),
                label = "mainTabCrossfade",
            ) { tab ->
                when (tab) {
                    MainTab.Home -> HomeScreen(homeViewModel, onOpenDetails)
                    MainTab.Saved -> FavoritesScreen(favoritesViewModel, onOpenDetails)
                }
            }
        }
    }
}
