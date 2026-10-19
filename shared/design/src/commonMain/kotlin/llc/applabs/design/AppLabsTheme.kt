package llc.applabs.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import llc.applabs.design.tokens.AppLabsDarkColorScheme
import llc.applabs.design.tokens.AppLabsLightColorScheme
import llc.applabs.design.tokens.AppLabsShapes
import llc.applabs.design.tokens.AppLabsTypography

// ---------------------------------------------------------------------------
// Theme entry point
// ---------------------------------------------------------------------------

/**
 * Root theme composable for all AppLabs applications.
 *
 * Wraps [MaterialTheme] with the AppLabs color scheme, typography, and shapes.
 * Use [useDarkTheme] to override the system setting (e.g. from a user preference).
 *
 * Usage:
 * ```kotlin
 * AppLabsTheme {
 *     // your content
 * }
 * ```
 */
@Composable
fun AppLabsTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (useDarkTheme) AppLabsDarkColorScheme else AppLabsLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppLabsTypography,
        shapes = AppLabsShapes,
        content = content,
    )
}
