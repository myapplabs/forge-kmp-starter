package com.forge.starter.data.repository

import com.forge.starter.domain.model.CounterModel
import com.forge.starter.domain.repository.CounterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * In-memory implementation of [CounterRepository].
 *
 * Architecture convention: repository implementations take their data sources
 * as constructor parameters — no static or global access.
 *
 * This implementation is a stub that demonstrates the pattern.
 * Persistence can be added in a future feature by injecting a local data source.
 */
class CounterRepositoryImpl(
    private val localDataSource: CounterLocalDataSource = InMemoryCounterLocalDataSource()
) : CounterRepository {

    override fun observeCounter(): Flow<CounterModel> = localDataSource.observeCounter()

    override suspend fun getCounter(): CounterModel = localDataSource.getCounter()

    override suspend fun increment() = localDataSource.increment()

    override suspend fun decrement() = localDataSource.decrement()

    override suspend fun reset() = localDataSource.reset()
}

/**
 * Interface for local counter data source.
 * Allows swapping in-memory with persisted storage later.
 */
interface CounterLocalDataSource {
    fun observeCounter(): Flow<CounterModel>
    suspend fun getCounter(): CounterModel
    suspend fun increment()
    suspend fun decrement()
    suspend fun reset()
}

/**
 * In-memory implementation of [CounterLocalDataSource].
 * Thread-safe using a Mutex.
 */
class InMemoryCounterLocalDataSource : CounterLocalDataSource {
    private val mutex = Mutex()
    private val _counter = MutableStateFlow(CounterModel(0))

    override fun observeCounter(): Flow<CounterModel> = _counter.asStateFlow()

    override suspend fun getCounter(): CounterModel = _counter.value

    override suspend fun increment() = mutex.withLock {
        _counter.value = _counter.value.copy(value = _counter.value.value + 1)
    }

    override suspend fun decrement() = mutex.withLock {
        _counter.value = _counter.value.copy(value = _counter.value.value - 1)
    }

    override suspend fun reset() = mutex.withLock {
        _counter.value = CounterModel(0)
    }
}
