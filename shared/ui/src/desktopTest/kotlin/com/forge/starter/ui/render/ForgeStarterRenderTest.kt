package com.forge.starter.ui.render


import kotlin.test.Test

/**
 * ForgeStarter's render catalog (FORGE render tier, docs/render-tier.md): every screen's content composable with fixed
 * sample states — no ViewModels, no clock, no randomness. Runs keep it current; FORGE checks the renders.
 */
val appCatalog = renderCatalog {

    // TODO CounterScreen: render its content composable with sample states (loaded, empty, error…)
}

class ForgeStarterRenderTest {
    @Test
    fun renderEveryScreen() {
        val rows = ForgeRender.renderAll(appCatalog)
        println("FORGE render: ${rows.count { it["status"] == "ok" }}/${rows.size} rendered → ${ForgeRender.outDir.absolutePath}")
    }
}
