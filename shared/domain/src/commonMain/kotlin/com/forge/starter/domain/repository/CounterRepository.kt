package com.forge.starter.domain.repository

import com.forge.starter.domain.model.CounterModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for counter operations.
 * Defined in :shared:domain — no implementation details here.
 */
interface CounterRepository {
    fun observeCounter(): Flow<CounterModel>
    suspend fun getCounter(): CounterModel
    suspend fun increment()
    suspend fun decrement()
    suspend fun reset()
}
