package com.example.wewatchmvc.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<State : Any, Intent : Any, Effect : Any> : ViewModel() {

    protected abstract val initialState: State

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state

    private val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect

    protected fun setState(reducer: State.() -> State) {
        _state.value = _state.value.reducer()
    }

    protected fun emitEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    abstract fun handleIntent(intent: Intent)
}