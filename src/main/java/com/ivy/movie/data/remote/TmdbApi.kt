package com.ivy.movie.data.remote

import com.ivy.movie.data.remote.dto.MediaDto
import com.ivy.movie.data.remote.dto.MediaListDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    @GET("trending/all/week")
    suspend fun trending(
        @Query("page") page: Int,
        @Query("language") language: String,
    ): MediaListDto

    @GET("search/multi")
    suspend fun searchMulti(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("language") language: String,
    ): MediaListDto

    @GET("movie/{id}")
    suspend fun movieDetails(
        @Path("id") id: Long,
        @Query("language") language: String,
    ): MediaDto

    @GET("tv/{id}")
    suspend fun tvDetails(
        @Path("id") id: Long,
        @Query("language") language: String,
    ): MediaDto
}
