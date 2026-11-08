package com.forge.starter.ui.render

import androidx.compose.runtime.Composable

/**
 * What FORGE's render harness provides around every render (docs/render-tier.md): the composition locals the
 * app's screens read that fail when nothing provides them. This file is yours — FORGE writes it once and keeps
 * your edits. Give a local sample data here (a fake with fixed content) and every screen reading it renders it.
 */
@Composable
fun RenderLocals(content: @Composable () -> Unit) {
    content()
}
