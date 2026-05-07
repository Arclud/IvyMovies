package com.ivy.movie.data.remote

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TmdbNetwork {
    fun api(accessToken: String): TmdbApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(TmdbApi::class.java)
    }
}
