package com.forge.starter.data

import com.forge.starter.data.repository.CounterRepositoryImpl
import com.forge.starter.data.repository.InMemoryCounterLocalDataSource
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CounterRepositoryImplTest {

    private fun createRepository() = CounterRepositoryImpl(
        localDataSource = InMemoryCounterLocalDataSource()
    )

    @Test
    fun `initial counter value is zero`() = runTest {
        val repo = createRepository()
        val counter = repo.getCounter()
        assertEquals(0, counter.value)
    }

    @Test
    fun `increment increases counter by one`() = runTest {
        val repo = createRepository()
        repo.increment()
        val counter = repo.getCounter()
        assertEquals(1, counter.value)
    }

    @Test
    fun `increment persists across multiple calls within session`() = runTest {
        val repo = createRepository()
        repo.increment()
        repo.increment()
        repo.increment()
        val counter = repo.getCounter()
        assertEquals(3, counter.value)
    }

    @Test
    fun `decrement decreases counter by one`() = runTest {
        val repo = createRepository()
        repo.increment()
        repo.increment()
        repo.decrement()
        val counter = repo.getCounter()
        assertEquals(1, counter.value)
    }

    @Test
    fun `reset sets counter to zero`() = runTest {
        val repo = createRepository()
        repo.increment()
        repo.increment()
        repo.reset()
        val counter = repo.getCounter()
        assertEquals(0, counter.value)
    }
}
