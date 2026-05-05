package com.example.wewatchmvc.ui.add

import com.example.wewatchmvc.core.Effect

sealed class AddEffect : Effect {
    data class ShowToast(val message: String) : AddEffect()
    object NavigateBack : AddEffect()
    object NavigateToSearch : AddEffect()
}