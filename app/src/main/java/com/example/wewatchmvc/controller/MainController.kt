package com.example.wewatch.controller

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.example.wewatch.model.Movie
import com.example.wewatch.model.MovieDatabase

class MainController(private val context: Context) {

    private val database = MovieDatabase.getDatabase(context)
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    interface MainCallback {
        fun onMoviesLoaded(movies: List<Movie>)
        fun onMovieAdded()
        fun onMoviesDeleted()
        fun onError(error: String)
    }

    private var callback: MainCallback? = null

    fun setCallback(callback: MainCallback) {
        this.callback = callback
    }

    fun loadMovies() {
        scope.launch(Dispatchers.IO) {
            try {
                database.movieDao().getAllMovies().collect { movies ->
                    scope.launch(Dispatchers.Main) {
                        callback?.onMoviesLoaded(movies)
                    }
                }
            } catch (e: Exception) {
                scope.launch(Dispatchers.Main) {
                    callback?.onError("Ошибка загрузки: ${e.message}")
                }
            }
        }
    }

    fun addMovie(movie: Movie) {
        scope.launch(Dispatchers.IO) {
            try {
                database.movieDao().insert(movie)
                scope.launch(Dispatchers.Main) {
                    callback?.onMovieAdded()
                    loadMovies()
                }
            } catch (e: Exception) {
                scope.launch(Dispatchers.Main) {
                    callback?.onError("Ошибка добавления: ${e.message}")
                }
            }
        }
    }

    fun deleteMovies(selectedMovies: List<Movie>) {
        scope.launch(Dispatchers.IO) {
            try {
                val imdbIds = selectedMovies.map { it.imdbId }
                database.movieDao().deleteMoviesByImdbIds(imdbIds)
                scope.launch(Dispatchers.Main) {
                    callback?.onMoviesDeleted()
                    loadMovies()
                }
            } catch (e: Exception) {
                scope.launch(Dispatchers.Main) {
                    callback?.onError("Ошибка удаления: ${e.message}")
                }
            }
        }
    }
}