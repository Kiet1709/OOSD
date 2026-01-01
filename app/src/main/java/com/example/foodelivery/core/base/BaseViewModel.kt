package com.example.foodelivery.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.core.base.ViewSideEffect
import com.example.foodelivery.core.base.ViewState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : ViewState, I : ViewIntent, E : ViewSideEffect>(initialState: S) : ViewModel() {

    // 1. STATE MANAGEMENT
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    protected val currentState: S get() = _uiState.value

    protected fun setState(reduce: S.() -> S) {
        _uiState.update { it.reduce() }
    }

    // 2. INTENT HANDLING
    private val _intent = Channel<I>(Channel.UNLIMITED)

    init {
        viewModelScope.launch {
            _intent.receiveAsFlow().collect { intent ->
                handleIntent(intent)
            }
        }
    }

    fun sendIntent(intent: I) {
        viewModelScope.launch { _intent.send(intent) }
    }

    protected abstract fun handleIntent(intent: I)

    // 3. SIDE EFFECT
    private val _effect = Channel<E>()
    val effect = _effect.receiveAsFlow()

    protected fun setEffect(builder: () -> E) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    // 4. ERROR HANDLING (FIREBASE SAFE)
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }

    protected fun launchCatching(
        block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(exceptionHandler) {
        block()
    }

    open fun handleError(error: Throwable) {
        error.printStackTrace()
    }
}