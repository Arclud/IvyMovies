package com.ivy.movie.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MediaListDto(
    @SerializedName("results") val results: List<MediaDto>,
)

data class MediaDto(
    @SerializedName("id") val id: Long,
    @SerializedName("media_type") val mediaType: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
)
