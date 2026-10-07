package com.forge.starter.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.forge.starter.app.counter.CounterViewModelWrapper
import com.forge.starter.ui.counter.CounterContent
import com.forge.starter.ui.navigation.Destination
import com.forge.starter.ui.theme.ForgeStarterTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for the Android application.
 *
 * Architecture convention: MainActivity only sets up the NavHost.
 * No business logic here — all logic lives in shared modules.
 *
 * The Hilt ViewModel wrapper is instantiated here; its UiState and onEvent
 * are passed into the shared Composable (CounterContent) from :shared:ui.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForgeStarterTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Destination.Counter.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(route = Destination.Counter.route) {
                            val viewModel: CounterViewModelWrapper = hiltViewModel()
                            val uiState by viewModel.uiState.collectAsState()
                            CounterContent(
                                uiState = uiState,
                                onEvent = viewModel::onEvent
                            )
                        }
                    }
                }
            }
        }
    }
}
