package com.forge.starter.domain.usecase

import com.forge.starter.domain.repository.CounterRepository

/**
 * Use case for resetting the counter to zero.
 */
class ResetCounterUseCase(
    private val counterRepository: CounterRepository
) {
    suspend operator fun invoke() {
        counterRepository.reset()
    }
}
