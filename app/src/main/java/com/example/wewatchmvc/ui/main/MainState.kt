package com.example.wewatchmvc.ui.main

import com.example.wewatchmvc.core.State
import com.example.wewatchmvc.model.Movie

sealed class MainState : State {
    object Loading : MainState()
    data class Success(val movies: List<Movie>, val selectedMovies: Set<String> = emptySet()) : MainState()
    data class Error(val message: String) : MainState()
    object Empty : MainState()
}