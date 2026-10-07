package com.forge.starter.app.di

import com.forge.starter.domain.repository.CounterRepository
import com.forge.starter.domain.usecase.DecrementCounterUseCase
import com.forge.starter.domain.usecase.GetCounterUseCase
import com.forge.starter.domain.usecase.IncrementCounterUseCase
import com.forge.starter.domain.usecase.ResetCounterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module providing domain-layer use cases.
 *
 * Architecture convention:
 * - Use cases are scoped to ViewModel — new instance per ViewModel.
 * - Use cases receive repository interfaces (not implementations) as parameters.
 */
@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {

    @Provides
    @ViewModelScoped
    fun provideGetCounterUseCase(
        counterRepository: CounterRepository
    ): GetCounterUseCase = GetCounterUseCase(counterRepository)

    @Provides
    @ViewModelScoped
    fun provideIncrementCounterUseCase(
        counterRepository: CounterRepository
    ): IncrementCounterUseCase = IncrementCounterUseCase(counterRepository)

    @Provides
    @ViewModelScoped
    fun provideDecrementCounterUseCase(
        counterRepository: CounterRepository
    ): DecrementCounterUseCase = DecrementCounterUseCase(counterRepository)

    @Provides
    @ViewModelScoped
    fun provideResetCounterUseCase(
        counterRepository: CounterRepository
    ): ResetCounterUseCase = ResetCounterUseCase(counterRepository)
}
