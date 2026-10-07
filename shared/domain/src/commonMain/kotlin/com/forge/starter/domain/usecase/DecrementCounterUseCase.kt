package com.forge.starter.domain.usecase

import com.forge.starter.domain.repository.CounterRepository

/**
 * Use case for decrementing the counter by 1.
 */
class DecrementCounterUseCase(
    private val counterRepository: CounterRepository
) {
    suspend operator fun invoke() {
        counterRepository.decrement()
    }
}
