package com.ivy.movie.di

import android.content.Context
import com.ivy.movie.BuildConfig
import com.ivy.movie.data.local.FavoriteMediaDao
import com.ivy.movie.data.local.MovieDatabase
import com.ivy.movie.data.remote.TmdbApi
import com.ivy.movie.data.remote.TmdbNetwork
import com.ivy.movie.data.repository.FavoritesRepositoryImpl
import com.ivy.movie.data.repository.MediaRepositoryImpl
import com.ivy.movie.domain.favorites.FavoritesRepository
import com.ivy.movie.domain.media.MediaItem
import com.ivy.movie.domain.media.MediaRepository
import com.ivy.movie.domain.media.MediaType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMovieDatabase(
        @ApplicationContext context: Context,
    ): MovieDatabase = MovieDatabase.create(context)

    @Provides
    fun provideFavoriteMediaDao(database: MovieDatabase): FavoriteMediaDao = database.favoriteDao()

    @Provides
    @Singleton
    fun provideTmdbApi(): TmdbApi = TmdbNetwork.api(BuildConfig.TMDB_ACCESS_TOKEN)

    @Provides
    @Singleton
    fun provideMediaRepository(api: TmdbApi): MediaRepository {
        return if (BuildConfig.TMDB_ACCESS_TOKEN.isBlank()) {
            DemoMediaRepository
        } else {
            MediaRepositoryImpl(api)
        }
    }

    @Provides
    @Singleton
    fun provideFavoritesRepository(dao: FavoriteMediaDao): FavoritesRepository = FavoritesRepositoryImpl(dao)
}

private object DemoMediaRepository : MediaRepository {
    private val items = listOf(
        MediaItem(
            id = 603,
            type = MediaType.Movie,
            title = "The Matrix",
            overview = "A hacker discovers that reality is a simulated world.",
            posterUrl = "",
            rating = 8.2,
            releaseDate = "1999-03-31",
        ),
        MediaItem(
            id = 70523,
            type = MediaType.Series,
            title = "Dark",
            overview = "A missing child sets four families on a frantic hunt for answers.",
            posterUrl = "",
            rating = 8.4,
            releaseDate = "2017-12-01",
        ),
    )

    override suspend fun popular(page: Int, language: String): List<MediaItem> = items

    override suspend fun search(query: String, page: Int, language: String): List<MediaItem> {
        return items.filter { it.title.contains(query, ignoreCase = true) }
    }

    override suspend fun details(id: Long, type: MediaType, language: String): MediaItem {
        return items.first { it.id == id && it.type == type }
    }
}
