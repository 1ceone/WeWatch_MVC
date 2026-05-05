package com.example.wewatchmvc.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wewatchmvc.model.Movie
import com.example.wewatchmvc.model.MovieDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MovieDatabase.getDatabase(application.applicationContext)
    private var allMovies: List<Movie> = emptyList()

    private val _state = MutableStateFlow<MainState>(MainState.Loading)
    val state: StateFlow<MainState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MainEffect>()
    val effect: SharedFlow<MainEffect> = _effect.asSharedFlow()

    init {
        loadMovies()
    }

    fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.LoadMovies -> loadMovies()
            is MainIntent.DeleteMovies -> deleteMovies(intent.movies)
            is MainIntent.SelectMovie -> selectMovie(intent.imdbId, intent.isSelected)
            is MainIntent.NavigateToAdd -> navigateToAdd()
        }
    }

    private fun loadMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            database.movieDao().getAllMovies().collectLatest { movies ->
                allMovies = movies
                if (movies.isEmpty()) {
                    _state.value = MainState.Empty
                } else {
                    val currentState = _state.value
                    val selectedIds = if (currentState is MainState.Success) {
                        currentState.selectedMovies
                    } else emptySet()
                    _state.value = MainState.Success(movies, selectedIds)
                }
            }
        }
    }

    private fun deleteMovies(movies: List<Movie>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imdbIds = movies.map { it.imdbId }
                database.movieDao().deleteMoviesByImdbIds(imdbIds)
                _effect.emit(MainEffect.ShowToast("Фильмы удалены"))
                loadMovies()
            } catch (e: Exception) {
                _state.value = MainState.Error("Ошибка удаления: ${e.message}")
            }
        }
    }

    private fun selectMovie(imdbId: String, isSelected: Boolean) {
        val currentState = _state.value
        if (currentState is MainState.Success) {
            val newSelected = if (isSelected) {
                currentState.selectedMovies + imdbId
            } else {
                currentState.selectedMovies - imdbId
            }
            _state.value = currentState.copy(selectedMovies = newSelected)
        }
    }

    private fun navigateToAdd() {
        viewModelScope.launch {
            _effect.emit(MainEffect.NavigateToAdd)
        }
    }
}