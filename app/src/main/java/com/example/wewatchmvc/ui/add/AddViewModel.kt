package com.example.wewatchmvc.ui.add

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
import kotlinx.coroutines.launch

class AddViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MovieDatabase.getDatabase(application.applicationContext)
    private var currentMovie: Movie? = null

    private val _state = MutableStateFlow<AddState>(AddState.Idle)
    val state: StateFlow<AddState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AddEffect>()
    val effect: SharedFlow<AddEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: AddIntent) {
        when (intent) {
            is AddIntent.SelectMovie -> selectMovie(intent.movie)
            is AddIntent.AddMovie -> addMovie()
            is AddIntent.NavigateToSearch -> navigateToSearch()
            is AddIntent.Reset -> reset()
        }
    }

    private fun selectMovie(movie: Movie) {
        currentMovie = movie
        _state.value = AddState.MovieSelected(movie)
    }

    private fun addMovie() {
        currentMovie?.let { movie ->
            viewModelScope.launch(Dispatchers.IO) {
                _state.value = AddState.Adding

                try {
                    database.movieDao().insert(movie)
                    _state.value = AddState.Success("Фильм добавлен")
                    _effect.emit(AddEffect.ShowToast("Фильм добавлен в список"))
                    _effect.emit(AddEffect.NavigateBack)
                } catch (e: Exception) {
                    _state.value = AddState.Error("Ошибка добавления: ${e.message}")
                    _effect.emit(AddEffect.ShowToast("Ошибка добавления: ${e.message}"))
                }
            }
        } ?: run {
            viewModelScope.launch {
                _effect.emit(AddEffect.ShowToast("Сначала выберите фильм"))
            }
        }
    }

    private fun navigateToSearch() {
        viewModelScope.launch {
            _effect.emit(AddEffect.NavigateToSearch)
        }
    }

    private fun reset() {
        currentMovie = null
        _state.value = AddState.Idle
    }
}