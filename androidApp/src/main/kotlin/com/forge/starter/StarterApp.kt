package com.forge.starter

import android.app.Application
import com.forge.starter.data.AppContainer

/** Holds the app's single [AppContainer] for the whole process. */
class StarterApp : Application() {
    val container: AppContainer by lazy { AppContainer() }
}
