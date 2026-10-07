package com.forge.starter.app.di

import com.forge.starter.data.repository.CounterRepositoryImpl
import com.forge.starter.data.repository.InMemoryCounterLocalDataSource
import com.forge.starter.domain.repository.CounterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing data-layer bindings.
 *
 * Architecture convention:
 * - All @Provides methods live in :app — shared modules do not depend on Hilt.
 * - Repository interfaces from :shared:domain are bound to implementations from :shared:data.
 * - Singleton scope ensures one repository per app lifetime.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideCounterRepository(): CounterRepository {
        return CounterRepositoryImpl(
            localDataSource = InMemoryCounterLocalDataSource()
        )
    }
}
