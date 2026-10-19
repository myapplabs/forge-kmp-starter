package com.forge.starter.ui.counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import llc.applabs.design.components.AppLabsPrimaryButton
import llc.applabs.design.components.AppLabsSecondaryButton
import llc.applabs.design.components.AppLabsTextButton
import llc.applabs.design.components.AppLabsTopBar
import llc.applabs.design.tokens.AppLabsSpacing

/**
 * Counter feature screen.
 *
 * Architecture convention: composables receive a ViewModel and collect state. They send events
 * back via [CounterUiEvent] — never call use cases directly. UI is built from `:shared:design`
 * components and tokens (see .forge/DESIGN_COMPONENTS.md), never raw Material components.
 */
@Composable
fun CounterScreen(
    viewModel: CounterViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    CounterContent(uiState = uiState, onEvent = viewModel::onEvent, modifier = modifier)
}

/** Stateless content — decoupled from the ViewModel for previews and tests. */
@Composable
fun CounterContent(
    uiState: CounterUiState,
    onEvent: (CounterUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { AppLabsTopBar(title = "Counter") },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppLabsSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "${uiState.count}", style = MaterialTheme.typography.displayLarge)
            Spacer(modifier = Modifier.height(AppLabsSpacing.xl))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                AppLabsSecondaryButton(text = "−", onClick = { onEvent(CounterUiEvent.Decrement) })
                AppLabsPrimaryButton(text = "+", onClick = { onEvent(CounterUiEvent.Increment) })
            }
            Spacer(modifier = Modifier.height(AppLabsSpacing.md))
            AppLabsTextButton(text = "Reset", onClick = { onEvent(CounterUiEvent.Reset) })
            uiState.error?.let { message ->
                Spacer(modifier = Modifier.height(AppLabsSpacing.md))
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
