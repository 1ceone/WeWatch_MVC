package com.example.wewatchmvc.ui.main

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.wewatchmvc.core.BaseViewModel
import com.example.wewatchmvc.model.Movie
import com.example.wewatchmvc.model.MovieDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(private val context: Context) : BaseViewModel<MainState, MainIntent, MainEffect>() {

    private val database = MovieDatabase.getDatabase(context)
    private var allMovies: List<Movie> = emptyList()

    override fun createInitialState(): MainState = MainState.Loading

    override fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.LoadMovies -> loadMovies()
            is MainIntent.DeleteMovies -> deleteMovies(intent.movies)
            is MainIntent.SelectMovie -> selectMovie(intent.imdbId, intent.isSelected)
            is MainIntent.NavigateToAdd -> navigateToAdd()
        }
    }

    private fun loadMovies() {
        viewModelScope.launch {
            database.movieDao().getAllMovies().collectLatest { movies ->
                allMovies = movies
                if (movies.isEmpty()) {
                    setState { MainState.Empty }
                } else {
                    val currentState = state.value
                    val selectedIds = if (currentState is MainState.Success) {
                        currentState.selectedMovies
                    } else emptySet()
                    setState { MainState.Success(movies, selectedIds) }
                }
            }
        }
    }

    private fun deleteMovies(movies: List<Movie>) {
        viewModelScope.launch {
            try {
                val imdbIds = movies.map { it.imdbId }
                database.movieDao().deleteMoviesByImdbIds(imdbIds)
                emitEffect(MainEffect.ShowToast("Фильмы удалены"))
                loadMovies()
            } catch (e: Exception) {
                setState { MainState.Error("Ошибка удаления: ${e.message}") }
            }
        }
    }

    private fun selectMovie(imdbId: String, isSelected: Boolean) {
        val currentState = state.value
        if (currentState is MainState.Success) {
            val newSelected = if (isSelected) {
                currentState.selectedMovies + imdbId
            } else {
                currentState.selectedMovies - imdbId
            }
            setState { currentState.copy(selectedMovies = newSelected) }
        }
    }

    private fun navigateToAdd() {
        emitEffect(MainEffect.NavigateToAdd)
    }
}