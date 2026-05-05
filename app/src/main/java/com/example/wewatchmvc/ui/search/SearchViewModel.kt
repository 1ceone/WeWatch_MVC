package com.example.wewatchmvc.ui.search

import androidx.lifecycle.viewModelScope
import com.example.wewatchmvc.core.BaseViewModel
import com.example.wewatchmvc.model.Movie
import com.example.wewatchmvc.model.SearchResult
import com.example.wewatchmvc.model.network.RetrofitInstance
import kotlinx.coroutines.launch

class SearchViewModel : BaseViewModel<SearchState, SearchIntent, SearchEffect>() {

    private val apiKey = "484c9232" // Замените на ваш ключ

    override val initialState: SearchState = SearchState.Idle

    override fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.Search -> searchMovies(intent.query, intent.year)
            is SearchIntent.SelectMovie -> selectMovie(intent.result)
            is SearchIntent.ClearResults -> clearResults()
        }
    }

    private fun searchMovies(query: String, year: String?) {
        viewModelScope.launch {
            setState { SearchState.Loading }

            try {
                val response = RetrofitInstance.api.searchMovies(query, year, apiKey)
                if (response.response == "True") {
                    setState { SearchState.Success(response.search ?: emptyList()) }
                } else {
                    setState { SearchState.Error(response.error ?: "Ничего не найдено") }
                }
            } catch (e: Exception) {
                setState { SearchState.Error("Ошибка сети: ${e.message}") }
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
        emitEffect(SearchEffect.NavigateBackWithMovie(movie))
    }

    private fun clearResults() {
        setState { SearchState.Idle }
    }
}