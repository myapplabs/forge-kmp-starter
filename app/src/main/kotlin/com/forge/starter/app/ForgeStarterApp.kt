package com.forge.starter.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for ForgeStarter.
 *
 * Architecture convention: @HiltAndroidApp triggers Hilt's code generation
 * and provides the root component for the dependency injection graph.
 */
@HiltAndroidApp
class ForgeStarterApp : Application()
