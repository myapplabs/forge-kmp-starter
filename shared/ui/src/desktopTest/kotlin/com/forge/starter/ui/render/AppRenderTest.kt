package com.forge.starter.ui.render

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.forge.starter.ui.counter.CounterContent
import com.forge.starter.ui.counter.CounterUiState
import llc.applabs.design.components.AppLabsBadge
import llc.applabs.design.components.AppLabsBadgeStyle
import llc.applabs.design.components.AppLabsCard
import llc.applabs.design.components.AppLabsDestructiveButton
import llc.applabs.design.components.AppLabsEmptyState
import llc.applabs.design.components.AppLabsErrorState
import llc.applabs.design.components.AppLabsPrimaryButton
import llc.applabs.design.components.AppLabsSecondaryButton
import llc.applabs.design.components.AppLabsTextButton
import llc.applabs.design.components.AppLabsTextField
import llc.applabs.design.components.AppLabsTopBar
import llc.applabs.design.tokens.AppLabsSpacing
import kotlin.test.Test

/**
 * The app's render catalog (FORGE render tier, docs/render-tier.md in FORGE): every screen's content composable
 * with fixed sample states — no ViewModels, no clock, no randomness — and the design components. verify.sh runs
 * it (allTests → desktopTest); FORGE checks the renders: invisible elements, contrast, touch targets, large font,
 * structure. Runs keep it current: a new screen or component gets an entry.
 */
val appCatalog = renderCatalog {
    screen("Counter") {
        state("zero") { CounterContent(uiState = CounterUiState(count = 0), onEvent = {}) }
        state("counted") { CounterContent(uiState = CounterUiState(count = 42), onEvent = {}) }
        state("error") { CounterContent(uiState = CounterUiState(count = 3, error = "Could not save the count."), onEvent = {}) }
    }
    component("AppLabsButton") {
        state("all") {
            Column(Modifier.padding(AppLabsSpacing.lg), verticalArrangement = Arrangement.spacedBy(AppLabsSpacing.md)) {
                AppLabsPrimaryButton(text = "Primary", onClick = {})
                AppLabsPrimaryButton(text = "Primary disabled", onClick = {}, enabled = false)
                AppLabsSecondaryButton(text = "Secondary", onClick = {})
                AppLabsDestructiveButton(text = "Delete", onClick = {})
                AppLabsTextButton(text = "Text button", onClick = {})
            }
        }
    }
    component("AppLabsTopBar") { state("title") { AppLabsTopBar(title = "Settings") } }
    component("AppLabsTextField") {
        state("filled") {
            Column(Modifier.padding(AppLabsSpacing.lg), verticalArrangement = Arrangement.spacedBy(AppLabsSpacing.md)) {
                AppLabsTextField(value = "Ada", onValueChange = {}, label = "Name")
                AppLabsTextField(value = "", onValueChange = {}, label = "Email", placeholder = "you@example.com")
                AppLabsTextField(value = "ada@", onValueChange = {}, label = "Email", error = "Enter a full email address")
            }
        }
    }
    component("AppLabsCard") {
        state("text") {
            AppLabsCard(Modifier.padding(AppLabsSpacing.lg)) {
                Text("Card title", Modifier.padding(AppLabsSpacing.md))
                Text("Supporting text inside a card.", Modifier.padding(AppLabsSpacing.md))
            }
        }
    }
    component("AppLabsBadge") {
        state("styles") {
            Column(Modifier.padding(AppLabsSpacing.lg), verticalArrangement = Arrangement.spacedBy(AppLabsSpacing.sm)) {
                AppLabsBadgeStyle.entries.filter { it != AppLabsBadgeStyle.Custom }.forEach { AppLabsBadge(text = it.name, style = it) }
            }
        }
    }
    component("AppLabsEmptyState") {
        state("action") { AppLabsEmptyState(title = "Nothing here yet", subtitle = "Items you add show up here.", actionLabel = "Add one", onAction = {}) }
    }
    component("AppLabsErrorState") { state("retry") { AppLabsErrorState(message = "Check your connection.", onRetry = {}) } }
}

class AppRenderTest {
    @Test
    fun renderEveryScreen() {
        val rows = ForgeRender.renderAll(appCatalog)
        println("FORGE render: ${rows.count { it["status"] == "ok" }}/${rows.size} rendered → ${ForgeRender.outDir.absolutePath}")
    }
}
