package com.forge.starter.domain

import com.forge.starter.domain.model.CounterModel
import com.forge.starter.domain.repository.CounterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory fake repository for use in domain-layer tests.
 */
class FakeCounterRepository(initialValue: Int = 0) : CounterRepository {
    private val _counter = MutableStateFlow(CounterModel(initialValue))

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
