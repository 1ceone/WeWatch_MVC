package com.example.wewatchmvc.ui.search

import com.example.wewatchmvc.core.Intent
import com.example.wewatchmvc.model.SearchResult

sealed class SearchIntent : Intent {
    data class Search(val query: String, val year: String?) : SearchIntent()
    data class SelectMovie(val result: SearchResult) : SearchIntent()
    object ClearResults : SearchIntent()
}