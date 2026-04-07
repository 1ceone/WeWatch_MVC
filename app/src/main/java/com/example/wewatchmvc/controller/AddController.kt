package com.example.wewatch.controller

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.example.wewatch.model.Movie
import com.example.wewatch.model.MovieDatabase

class AddController(private val context: Context) {

    private val database = MovieDatabase.getDatabase(context)
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    interface AddCallback {
        fun onMovieAdded()
        fun onError(error: String)
    }

    private var callback: AddCallback? = null

    fun setCallback(callback: AddCallback) {
        this.callback = callback
    }

    fun addMovie(movie: Movie) {
        scope.launch(Dispatchers.IO) {
            try {
                // Проверяем, есть ли уже такой фильм в базе
                // Для простоты просто добавляем
                database.movieDao().insert(movie)
                scope.launch(Dispatchers.Main) {
                    callback?.onMovieAdded()
                }
            } catch (e: Exception) {
                scope.launch(Dispatchers.Main) {
                    callback?.onError("Ошибка добавления: ${e.message}")
                }
            }
        }
    }
}