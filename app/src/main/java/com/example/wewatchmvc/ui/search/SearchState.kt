package com.example.wewatchmvc.ui.search

import com.example.wewatchmvc.core.State
import com.example.wewatchmvc.model.SearchResult

sealed class SearchState : State {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val results: List<SearchResult>) : SearchState()
    data class Error(val message: String) : SearchState()
}