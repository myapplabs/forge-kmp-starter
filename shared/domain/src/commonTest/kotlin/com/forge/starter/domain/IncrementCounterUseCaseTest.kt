package com.forge.starter.domain

import com.forge.starter.domain.usecase.IncrementCounterUseCase
import com.forge.starter.domain.usecase.GetCounterUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IncrementCounterUseCaseTest {

    private val repository = FakeCounterRepository(initialValue = 0)
    private val incrementUseCase = IncrementCounterUseCase(repository)
    private val getUseCase = GetCounterUseCase(repository)

    @Test
    fun `invoke increments counter by one`() = runTest {
        incrementUseCase()
        val result = getUseCase()
        assertEquals(1, result.value)
    }

    @Test
    fun `invoke increments counter multiple times correctly`() = runTest {
        incrementUseCase()
        incrementUseCase()
        incrementUseCase()
        val result = getUseCase()
        assertEquals(3, result.value)
    }

    @Test
    fun `invoke increments from non-zero value`() = runTest {
        val repo = FakeCounterRepository(initialValue = 10)
        val increment = IncrementCounterUseCase(repo)
        val get = GetCounterUseCase(repo)
        increment()
        val result = get()
        assertEquals(11, result.value)
    }
}
