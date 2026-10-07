package com.forge.starter.domain.usecase

import com.forge.starter.domain.model.CounterModel
import com.forge.starter.domain.repository.CounterRepository

/**
 * Use case for retrieving the current counter value.
 *
 * Convention: every use case has a single public method — operator fun invoke().
 * This allows calling it as a function: getCounterUseCase()
 */
class GetCounterUseCase(
    private val counterRepository: CounterRepository
) {
    suspend operator fun invoke(): CounterModel {
        return counterRepository.getCounter()
    }
}
