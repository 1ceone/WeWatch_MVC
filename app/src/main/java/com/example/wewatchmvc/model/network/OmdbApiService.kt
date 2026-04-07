package com.example.wewatch.model.network

import com.example.wewatch.model.OmdbSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApiService {
    @GET("/")
    suspend fun searchMovies(
        @Query("s") query: String,
        @Query("y") year: String? = null,
        @Query("apikey") apiKey: String
    ): OmdbSearchResponse
}