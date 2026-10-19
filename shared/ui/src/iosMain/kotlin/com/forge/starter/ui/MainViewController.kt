package com.forge.starter.ui

import androidx.compose.ui.window.ComposeUIViewController
import com.forge.starter.data.AppContainer

/** Entry point used by iosApp/iosApp/ContentView.swift. */
fun MainViewController() = ComposeUIViewController { App(deps = AppContainer()) }
