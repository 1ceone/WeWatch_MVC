package com.example.wewatchmvc.ui.add

import com.example.wewatchmvc.core.Intent
import com.example.wewatchmvc.model.Movie

sealed class AddIntent : Intent {
    data class SelectMovie(val movie: Movie) : AddIntent()
    object AddMovie : AddIntent()
    object NavigateToSearch : AddIntent()
    object Reset : AddIntent()
}