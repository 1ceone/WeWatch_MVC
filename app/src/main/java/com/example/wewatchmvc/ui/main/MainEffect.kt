package com.example.wewatchmvc.ui.main

import com.example.wewatchmvc.core.Effect

sealed class MainEffect : Effect {
    data class ShowToast(val message: String) : MainEffect()
    object NavigateToAdd : MainEffect()
}