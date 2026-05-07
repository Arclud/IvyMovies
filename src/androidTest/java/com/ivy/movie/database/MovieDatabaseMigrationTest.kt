package com.ivy.movie.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivy.movie.data.local.MovieDatabase
import com.ivy.movie.data.local.MovieMigrations
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieDatabaseMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation(),
        databaseClass = MovieDatabase::class.java,
        specs = emptyList(),
        openFactory = FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate1To2KeepsFavoriteDataAndAddsCachedAt() {
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL(
                """
                INSERT INTO favorite_media(id, type, title, overview, posterUrl, rating, releaseDate)
                VALUES(603, 'movie', 'The Matrix', 'A hacker.', '', 8.2, '1999-03-31')
                """.trimIndent()
            )
            close()
        }

        val database = helper.runMigrationsAndValidate(
            TEST_DB,
            2,
            true,
            MovieMigrations.MIGRATION_1_2
        )

        val cursor = database.query("SELECT title, cachedAt FROM favorite_media WHERE id = 603")
        cursor.moveToFirst()
        assertEquals("The Matrix", cursor.getString(0))
        assertEquals(0L, cursor.getLong(1))
        cursor.close()
    }

    companion object {
        private const val TEST_DB = "movie-migration-test"
    }
}
