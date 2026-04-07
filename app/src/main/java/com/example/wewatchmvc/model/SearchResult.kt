package com.example.wewatch.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SearchResult(
    @SerializedName("Title")
    val title: String? = "",
    @SerializedName("Year")
    val year: String? = "",
    @SerializedName("imdbID")
    val imdbId: String? = "",
    @SerializedName("Poster")
    val posterUrl: String? = "",
    @SerializedName("Genre")
    val genre: String? = ""
) : Serializable

data class OmdbSearchResponse(
    @SerializedName("Search")
    val search: List<SearchResult>? = null,
    @SerializedName("Response")
    val response: String = "",
    @SerializedName("Error")
    val error: String? = null
)