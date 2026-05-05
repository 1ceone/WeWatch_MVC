package com.example.wewatchmvc.ui.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wewatchmvc.model.Movie
import com.example.wewatchmvc.model.SearchResult
import com.example.wewatchmvc.model.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val apiKey = "484c9232"  // ЗАМЕНИТЕ НА ВАШ КЛЮЧ!

    private val _state = MutableStateFlow<SearchState>(SearchState.Idle)
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SearchEffect>()
    val effect: SharedFlow<SearchEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.Search -> searchMovies(intent.query, intent.year)
            is SearchIntent.SelectMovie -> selectMovie(intent.result)
            is SearchIntent.ClearResults -> clearResults()
        }
    }

    private fun searchMovies(query: String, year: String?) {
        viewModelScope.launch {
            _state.value = SearchState.Loading
            Log.d("SearchViewModel", "Поиск: $query, год: $year")

            try {
                Log.d("SearchViewModel", "Запрос к API...")
                val response = RetrofitInstance.api.searchMovies(query, year, apiKey)
                Log.d("SearchViewModel", "Ответ: ${response.response}")
                Log.d("SearchViewModel", "Результатов: ${response.search?.size ?: 0}")

                if (response.response == "True") {
                    _state.value = SearchState.Success(response.search ?: emptyList())
                } else {
                    val errorMsg = response.error ?: "Ничего не найдено"
                    Log.e("SearchViewModel", "Ошибка: $errorMsg")
                    _state.value = SearchState.Error(errorMsg)
                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Исключение: ${e.message}", e)
                _state.value = SearchState.Error("Ошибка сети: ${e.message}")
            }
        }
    }

    private fun selectMovie(result: SearchResult) {
        val movie = Movie(
            title = result.title ?: "",
            year = result.year ?: "",
            posterUrl = result.posterUrl ?: "",
            imdbId = result.imdbId ?: ""
        )
        viewModelScope.launch {
            _effect.emit(SearchEffect.NavigateBackWithMovie(movie))
        }
    }

    private fun clearResults() {
        _state.value = SearchState.Idle
    }
}