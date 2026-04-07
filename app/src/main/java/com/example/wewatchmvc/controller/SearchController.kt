package com.example.wewatchmvc.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.example.wewatchmvc.model.SearchResult
import com.example.wewatchmvc.model.network.RetrofitInstance

class SearchController {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    interface SearchCallback {
        fun onSearchResults(results: List<SearchResult>)
        fun onLoading(isLoading: Boolean)
        fun onError(error: String)
    }

    private var callback: SearchCallback? = null
    private val apiKey = "484c9232" // ЗАМЕНИТЕ НА ВАШ КЛЮЧ

    fun setCallback(callback: SearchCallback) {
        this.callback = callback
    }

    fun searchMovies(query: String, year: String? = null) {
        scope.launch {
            callback?.onLoading(true)

            try {
                val response = RetrofitInstance.api.searchMovies(query, year, apiKey)
                if (response.response == "True") {
                    callback?.onSearchResults(response.search ?: emptyList())
                } else {
                    callback?.onError(response.error ?: "Ничего не найдено")
                    callback?.onSearchResults(emptyList())
                }
            } catch (e: Exception) {
                callback?.onError("Ошибка сети: ${e.message}")
                callback?.onSearchResults(emptyList())
            } finally {
                callback?.onLoading(false)
            }
        }
    }
}