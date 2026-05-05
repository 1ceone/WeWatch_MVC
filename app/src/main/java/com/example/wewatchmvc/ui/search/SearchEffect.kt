package com.example.wewatchmvc.ui.search

import com.example.wewatchmvc.core.Effect
import com.example.wewatchmvc.model.Movie

sealed class SearchEffect : Effect {
    data class NavigateBackWithMovie(val movie: Movie) : SearchEffect()
    data class ShowToast(val message: String) : SearchEffect()
}