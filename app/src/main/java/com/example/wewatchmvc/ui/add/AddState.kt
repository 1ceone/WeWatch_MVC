package com.example.wewatchmvc.ui.add

import com.example.wewatchmvc.core.State
import com.example.wewatchmvc.model.Movie

sealed class AddState : State {
    object Idle : AddState()
    data class MovieSelected(val movie: Movie) : AddState()
    object Adding : AddState()
    data class Success(val message: String) : AddState()
    data class Error(val message: String) : AddState()
}