package com.forge.starter.domain

import com.forge.starter.domain.usecase.GetCounterUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetCounterUseCaseTest {

    private val repository = FakeCounterRepository(initialValue = 5)
    private val useCase = GetCounterUseCase(repository)

    @Test
    fun `invoke returns current counter value`() = runTest {
        val result = useCase()
        assertEquals(5, result.value)
    }

    @Test
    fun `invoke returns default counter value when not set`() = runTest {
        val defaultRepo = FakeCounterRepository()
        val defaultUseCase = GetCounterUseCase(defaultRepo)
        val result = defaultUseCase()
        assertEquals(0, result.value)
    }
}
