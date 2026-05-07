package com.ivy.movie.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ivy.movie.data.local.entity.FavoriteMediaEntity

@Database(
    entities = [FavoriteMediaEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteMediaDao

    companion object {
        fun create(context: Context): MovieDatabase {
            return Room.databaseBuilder(
                context,
                MovieDatabase::class.java,
                "ivy-movies.db",
            )
                .addMigrations(MovieMigrations.MIGRATION_1_2)
                .build()
        }
    }
}

object MovieMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE favorite_media ADD COLUMN cachedAt INTEGER NOT NULL DEFAULT 0")
        }
    }
}
