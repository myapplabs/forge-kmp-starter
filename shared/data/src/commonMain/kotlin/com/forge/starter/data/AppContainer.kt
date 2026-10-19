package com.forge.starter.data

import com.forge.starter.data.repository.CounterRepositoryImpl
import com.forge.starter.domain.AppDependencies
import com.forge.starter.domain.repository.CounterRepository

/**
 * The app's object graph: creates every repository and service once.
 * Built by the platform entry points and passed to the UI as [AppDependencies].
 */
class AppContainer(
    override val counterRepository: CounterRepository = CounterRepositoryImpl(),
) : AppDependencies
