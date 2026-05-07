package com.ivy.movie.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ivy.movie.data.local.MovieDatabase
import com.ivy.movie.data.local.entity.FavoriteMediaEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test

class FavoriteDaoTest {

    private lateinit var database: MovieDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MovieDatabase::class.java
        ).build()
    }

    @After
    fun close() {
        database.close()
    }

    @Test
    fun saveReadAndDeleteFavorite() = runTest {
        val entity = FavoriteMediaEntity(603, "movie", "The Matrix", "A hacker.", "", 8.2, "1999-03-31")

        database.favoriteDao().upsert(entity)
        assertEquals(listOf(entity), database.favoriteDao().observeAll().first())

        database.favoriteDao().delete(id = 603, type = "movie")
        assertEquals(emptyList<FavoriteMediaEntity>(), database.favoriteDao().observeAll().first())
    }
}
