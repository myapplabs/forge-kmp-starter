package com.forge.starter.ui.counter

/**
 * Defines the contract (UiState, UiEvent, UiEffect) for the Counter feature.
 *
 * Architecture convention:
 * - UiState: immutable snapshot of what the UI should render
 * - UiEvent: user actions sent into the ViewModel
 * - UiEffect: one-shot side effects (navigation, toasts) sent out via Channel
 */

data class CounterUiState(
    val count: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class CounterUiEvent {
    data object Increment : CounterUiEvent()
    data object Decrement : CounterUiEvent()
    data object Reset : CounterUiEvent()
}

sealed class CounterUiEffect {
    data class ShowMessage(val message: String) : CounterUiEffect()
}
