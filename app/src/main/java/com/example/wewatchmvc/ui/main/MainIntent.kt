package com.example.wewatchmvc.ui.main

import com.example.wewatchmvc.core.Intent
import com.example.wewatchmvc.model.Movie

sealed class MainIntent : Intent {
    object LoadMovies : MainIntent()
    data class DeleteMovies(val movies: List<Movie>) : MainIntent()
    data class SelectMovie(val imdbId: String, val isSelected: Boolean) : MainIntent()
    object NavigateToAdd : MainIntent()
}