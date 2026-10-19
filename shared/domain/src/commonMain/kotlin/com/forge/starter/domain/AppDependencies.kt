package com.forge.starter.domain

import com.forge.starter.domain.repository.CounterRepository

/**
 * Everything the UI needs from the data layer, as domain interfaces.
 *
 * Architecture convention: `:shared:ui` depends on this interface only. The app's entry points
 * (Android `StarterApp`, iOS `MainViewController`) create the concrete `AppContainer` from
 * `:shared:data` and pass it in — manual dependency injection, no DI framework.
 * Add a property here for each new repository or service.
 */
interface AppDependencies {
    val counterRepository: CounterRepository
}
