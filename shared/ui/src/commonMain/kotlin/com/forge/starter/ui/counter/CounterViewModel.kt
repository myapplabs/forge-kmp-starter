package com.forge.starter.ui.counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forge.starter.domain.usecase.DecrementCounterUseCase
import com.forge.starter.domain.usecase.GetCounterUseCase
import com.forge.starter.domain.usecase.IncrementCounterUseCase
import com.forge.starter.domain.usecase.ResetCounterUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Counter feature.
 *
 * Architecture conventions:
 * - Exposes [StateFlow]<[CounterUiState]> for the UI to observe
 * - Accepts [CounterUiEvent] via [onEvent] to handle user interactions
 * - Emits [CounterUiEffect] via a [Channel] for one-shot side effects
 *
 * Constructor-injected use cases — no direct repository access.
 */
class CounterViewModel(
    private val getCounterUseCase: GetCounterUseCase,
    private val incrementCounterUseCase: IncrementCounterUseCase,
    private val decrementCounterUseCase: DecrementCounterUseCase,
    private val resetCounterUseCase: ResetCounterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CounterUiState())
    val uiState: StateFlow<CounterUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<CounterUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        loadCounter()
    }

    fun onEvent(event: CounterUiEvent) {
        when (event) {
            is CounterUiEvent.Increment -> handleIncrement()
            is CounterUiEvent.Decrement -> handleDecrement()
            is CounterUiEvent.Reset -> handleReset()
        }
    }

    private fun loadCounter() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val counter = getCounterUseCase()
                _uiState.update { it.copy(count = counter.value, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun handleIncrement() {
        viewModelScope.launch {
            try {
                incrementCounterUseCase()
                val counter = getCounterUseCase()
                _uiState.update { it.copy(count = counter.value, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
                _uiEffect.send(CounterUiEffect.ShowMessage("Failed to increment: ${e.message}"))
            }
        }
    }

    private fun handleDecrement() {
        viewModelScope.launch {
            try {
                decrementCounterUseCase()
                val counter = getCounterUseCase()
                _uiState.update { it.copy(count = counter.value, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
                _uiEffect.send(CounterUiEffect.ShowMessage("Failed to decrement: ${e.message}"))
            }
        }
    }

    private fun handleReset() {
        viewModelScope.launch {
            try {
                resetCounterUseCase()
                val counter = getCounterUseCase()
                _uiState.update { it.copy(count = counter.value, error = null) }
                _uiEffect.send(CounterUiEffect.ShowMessage("Counter reset"))
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
                _uiEffect.send(CounterUiEffect.ShowMessage("Failed to reset: ${e.message}"))
            }
        }
    }
}
