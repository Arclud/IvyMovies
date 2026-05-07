package com.ivy.movie.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ivy.movie.data.local.entity.FavoriteMediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMediaDao {
    @Query("SELECT * FROM favorite_media ORDER BY cachedAt DESC")
    fun observeAll(): Flow<List<FavoriteMediaEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_media WHERE id = :id AND type = :type)")
    suspend fun exists(id: Long, type: String): Boolean

    @Upsert
    suspend fun upsert(entity: FavoriteMediaEntity)

    @Query("DELETE FROM favorite_media WHERE id = :id AND type = :type")
    suspend fun delete(id: Long, type: String)
}
