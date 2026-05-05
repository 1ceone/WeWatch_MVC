package com.example.wewatchmvc.ui.add

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.wewatchmvc.core.BaseViewModel
import com.example.wewatchmvc.model.Movie
import com.example.wewatchmvc.model.MovieDatabase
import kotlinx.coroutines.launch

class AddViewModel(private val context: Context) : BaseViewModel<AddState, AddIntent, AddEffect>() {

    private val database = MovieDatabase.getDatabase(context)
    private var currentMovie: Movie? = null

    override val initialState: AddState = AddState.Idle

    override fun handleIntent(intent: AddIntent) {
        when (intent) {
            is AddIntent.SelectMovie -> selectMovie(intent.movie)
            is AddIntent.AddMovie -> addMovie()
            is AddIntent.NavigateToSearch -> navigateToSearch()
            is AddIntent.Reset -> reset()
        }
    }

    private fun selectMovie(movie: Movie) {
        currentMovie = movie
        setState { AddState.MovieSelected(movie) }
    }

    private fun addMovie() {
        currentMovie?.let { movie ->
            viewModelScope.launch {
                setState { AddState.Adding }

                try {
                    database.movieDao().insert(movie)
                    setState { AddState.Success("Фильм добавлен") }
                    emitEffect(AddEffect.ShowToast("Фильм добавлен в список"))
                    emitEffect(AddEffect.NavigateBack)
                } catch (e: Exception) {
                    setState { AddState.Error("Ошибка добавления: ${e.message}") }
                    emitEffect(AddEffect.ShowToast("Ошибка добавления: ${e.message}"))
                }
            }
        } ?: emitEffect(AddEffect.ShowToast("Сначала выберите фильм"))
    }

    private fun navigateToSearch() {
        emitEffect(AddEffect.NavigateToSearch)
    }

    private fun reset() {
        currentMovie = null
        setState { AddState.Idle }
    }
}