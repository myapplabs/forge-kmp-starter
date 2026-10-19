package com.forge.starter.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.forge.starter.domain.AppDependencies
import com.forge.starter.domain.usecase.DecrementCounterUseCase
import com.forge.starter.domain.usecase.GetCounterUseCase
import com.forge.starter.domain.usecase.IncrementCounterUseCase
import com.forge.starter.domain.usecase.ResetCounterUseCase
import com.forge.starter.ui.counter.CounterScreen
import com.forge.starter.ui.counter.CounterViewModel
import llc.applabs.design.AppLabsTheme

/** Navigation destinations. Add one per screen. */
object Routes {
    const val COUNTER = "counter"
}

/**
 * The whole app UI, shared by Android and iOS. The theme is applied once, here.
 * ViewModels are created per destination from [deps].
 */
@Composable
fun App(deps: AppDependencies) {
    AppLabsTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Routes.COUNTER) {
            composable(Routes.COUNTER) {
                val viewModel = viewModel {
                    CounterViewModel(
                        getCounterUseCase = GetCounterUseCase(deps.counterRepository),
                        incrementCounterUseCase = IncrementCounterUseCase(deps.counterRepository),
                        decrementCounterUseCase = DecrementCounterUseCase(deps.counterRepository),
                        resetCounterUseCase = ResetCounterUseCase(deps.counterRepository),
                    )
                }
                CounterScreen(viewModel = viewModel)
            }
        }
    }
}
