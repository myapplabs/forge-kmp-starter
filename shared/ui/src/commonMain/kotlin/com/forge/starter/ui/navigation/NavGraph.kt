package com.forge.starter.ui.navigation

/**
 * Navigation destinations for the app.
 *
 * Architecture convention: all navigation routes are defined here as sealed objects.
 * Each destination has a [route] string used with Compose Navigation.
 *
 * To add a new destination:
 * 1. Add a new object to [Destination]
 * 2. Add the composable to the NavHost in :app's MainActivity
 */
sealed class Destination(val route: String) {
    data object Counter : Destination("counter")
}
