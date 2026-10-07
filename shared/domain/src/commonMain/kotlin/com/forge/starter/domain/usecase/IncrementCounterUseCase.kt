package com.forge.starter.domain.usecase

import com.forge.starter.domain.repository.CounterRepository

/**
 * Use case for incrementing the counter by 1.
 *
 * Convention: every use case has a single public method — operator fun invoke().
 */
class IncrementCounterUseCase(
    private val counterRepository: CounterRepository
) {
    suspend operator fun invoke() {
        counterRepository.increment()
    }
}
