package com.forge.starter.app.counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forge.starter.domain.usecase.DecrementCounterUseCase
import com.forge.starter.domain.usecase.GetCounterUseCase
import com.forge.starter.domain.usecase.IncrementCounterUseCase
import com.forge.starter.domain.usecase.ResetCounterUseCase
import com.forge.starter.ui.counter.CounterUiEffect
import com.forge.starter.ui.counter.CounterUiEvent
import com.forge.starter.ui.counter.CounterUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Android-specific ViewModel annotated with @HiltViewModel.
 *
 * Architecture convention: The shared CounterViewModel in :shared:ui cannot use
 * @HiltViewModel because it's KMP code. This thin Android wrapper lives in :app,
 * gets injected by Hilt, and delegates to the shared logic.
 *
 * All business logic remains in :shared:ui — this class only adds the Hilt annotation.
 */
@HiltViewModel
class CounterViewModelWrapper @Inject constructor(
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
