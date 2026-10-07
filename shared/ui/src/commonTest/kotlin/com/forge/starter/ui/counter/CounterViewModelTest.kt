package com.forge.starter.ui.counter

import com.forge.starter.domain.model.CounterModel
import com.forge.starter.domain.repository.CounterRepository
import com.forge.starter.domain.usecase.DecrementCounterUseCase
import com.forge.starter.domain.usecase.GetCounterUseCase
import com.forge.starter.domain.usecase.IncrementCounterUseCase
import com.forge.starter.domain.usecase.ResetCounterUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import app.cash.turbine.test
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class CounterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeRepository: FakeCounterRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeCounterRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = CounterViewModel(
        getCounterUseCase = GetCounterUseCase(fakeRepository),
        incrementCounterUseCase = IncrementCounterUseCase(fakeRepository),
        decrementCounterUseCase = DecrementCounterUseCase(fakeRepository),
        resetCounterUseCase = ResetCounterUseCase(fakeRepository)
    )

    @Test
    fun `initial state has count zero and not loading`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(0, state.count)
        assertFalse(state.isLoading)
    }

    @Test
    fun `increment event increases count by one`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        // Initial load complete — count is 0

        viewModel.onEvent(CounterUiEvent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.count)
    }

    @Test
    fun `decrement event decreases count by one`() = runTest {
        fakeRepository.setCount(2)
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        // After load, count should be 2
        assertEquals(2, viewModel.uiState.value.count)

        viewModel.onEvent(CounterUiEvent.Decrement)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.count)
    }

    @Test
    fun `reset event sets count to zero`() = runTest {
        fakeRepository.setCount(5)
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(5, viewModel.uiState.value.count)

        viewModel.onEvent(CounterUiEvent.Reset)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.count)
    }

    @Test
    fun `reset event emits ShowMessage effect`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.onEvent(CounterUiEvent.Reset)
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertEquals(CounterUiEffect.ShowMessage("Counter reset"), effect)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `multiple increments accumulate correctly`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(CounterUiEvent.Increment)
        viewModel.onEvent(CounterUiEvent.Increment)
        viewModel.onEvent(CounterUiEvent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.count)
    }

    @Test
    fun `state flow emits updated count after increment`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            // consume current state
            val current = awaitItem()
            assertEquals(0, current.count)

            viewModel.onEvent(CounterUiEvent.Increment)
            testDispatcher.scheduler.advanceUntilIdle()

            val updated = awaitItem()
            assertEquals(1, updated.count)

            cancelAndIgnoreRemainingEvents()
        }
    }
}

/**
 * Fake CounterRepository for ViewModel tests.
 */
private class FakeCounterRepository : CounterRepository {
    private val _counter = MutableStateFlow(CounterModel(0))

    fun setCount(value: Int) {
        _counter.value = CounterModel(value)
    }

    override fun observeCounter(): Flow<CounterModel> = _counter.asStateFlow()

    override suspend fun getCounter(): CounterModel = _counter.value

    override suspend fun increment() {
        _counter.value = _counter.value.copy(value = _counter.value.value + 1)
    }

    override suspend fun decrement() {
        _counter.value = _counter.value.copy(value = _counter.value.value - 1)
    }

    override suspend fun reset() {
        _counter.value = CounterModel(0)
    }
}
