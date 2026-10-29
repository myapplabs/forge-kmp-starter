package llc.applabs.design.tokens

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Brand palette — Facebook-inspired blue, adjusted so text meets WCAG AA (4.5:1) — checked by FORGE's
// render tier (RENDER-003) on every verification
// ---------------------------------------------------------------------------

// Primary blues
val Blue10 = Color(0xFF001945)
val Blue20 = Color(0xFF002F73)
val Blue30 = Color(0xFF0E5FC0)
val Blue40 = Color(0xFF0D68DF)  // primary blue — WCAG AA with white text and as text on Neutral90 (was #1877F2, 4.2:1)
val Blue50 = Color(0xFF358CFF)  // lighter, for dark themes — AA as text on Surface10/20 (was #2D88FF)
val Blue60 = Color(0xFF5BA4FF)
val Blue70 = Color(0xFF8DC2FF)
val Blue80 = Color(0xFFB8D8FF)
val Blue90 = Color(0xFFDAECFF)
val Blue95 = Color(0xFFEDF4FF)

// Neutrals
val Neutral0  = Color(0xFF000000)
val Neutral5  = Color(0xFF0D0D0D)
val Neutral10 = Color(0xFF1C1E21)  // Facebook dark text
val Neutral20 = Color(0xFF3A3B3C)  // Facebook dark surface variant
val Neutral30 = Color(0xFF65676B)  // Facebook secondary text
val Neutral40 = Color(0xFF8A8D91)  // Facebook hint text
val Neutral50 = Color(0xFFB0B3B8)  // Facebook disabled text (dark)
val Neutral60 = Color(0xFFCED0D4)  // Facebook divider / border
val Neutral70 = Color(0xFFDADDE1)  // Light borders
val Neutral80 = Color(0xFFE4E6EB)  // Facebook light text (dark mode)
val Neutral90 = Color(0xFFF0F2F5)  // Facebook light background
val Neutral95 = Color(0xFFF7F8FA)
val Neutral99 = Color(0xFFFFFFFF)

// Surfaces (dark mode)
val Surface10 = Color(0xFF18191A)  // Facebook dark background
val Surface20 = Color(0xFF242526)  // Facebook dark surface
val Surface30 = Color(0xFF3A3B3C)  // Facebook dark surface variant

// Semantic
val ErrorRed   = Color(0xFFDB050C)  // error red — AA as text and under white text (was #FA383E, 3.3:1)
val ErrorLight = Color(0xFFFFEDED)
val SuccessGreen = Color(0xFF30851F)  // success green — AA under white text (was #42B72A, 2.6:1)
val SuccessGreenBright = Color(0xFF42B72A)  // dark themes, under black text (8.0:1)
val WarningAmber = Color(0xFFF7B928)

// ---------------------------------------------------------------------------
// Light color scheme
// ---------------------------------------------------------------------------

val AppLabsLightColorScheme = lightColorScheme(
    primary           = Blue40,
    onPrimary         = Neutral99,
    primaryContainer  = Blue90,
    onPrimaryContainer = Blue10,

    secondary         = Blue30,
    onSecondary       = Neutral99,
    secondaryContainer = Blue95,
    onSecondaryContainer = Blue20,

    tertiary          = SuccessGreen,
    onTertiary        = Neutral99,

    error             = ErrorRed,
    onError           = Neutral99,
    errorContainer    = ErrorLight,
    onErrorContainer  = Color(0xFF93000A),

    background        = Neutral90,       // Facebook light gray background
    onBackground      = Neutral10,

    surface           = Neutral99,       // White cards
    onSurface         = Neutral10,
    surfaceVariant    = Neutral80,
    onSurfaceVariant  = Neutral30,

    outline           = Neutral60,
    outlineVariant    = Neutral70,

    inverseSurface    = Neutral10,
    inverseOnSurface  = Neutral90,
    inversePrimary    = Blue50,

    scrim             = Neutral0,
)

// ---------------------------------------------------------------------------
// Dark color scheme
// ---------------------------------------------------------------------------

val AppLabsDarkColorScheme = darkColorScheme(
    primary           = Blue50,
    onPrimary         = Blue10,
    primaryContainer  = Blue30,
    onPrimaryContainer = Blue90,

    secondary         = Blue60,
    onSecondary       = Blue20,
    secondaryContainer = Blue30,
    onSecondaryContainer = Blue80,

    tertiary          = SuccessGreenBright,
    onTertiary        = Neutral0,

    error             = Color(0xFFFFB4AB),
    onError           = Color(0xFF690005),
    errorContainer    = Color(0xFF93000A),
    onErrorContainer  = Color(0xFFFFDAD6),

    background        = Surface10,       // Facebook dark background
    onBackground      = Neutral80,

    surface           = Surface20,       // Facebook dark card surface
    onSurface         = Neutral80,
    surfaceVariant    = Surface30,
    onSurfaceVariant  = Neutral50,

    outline           = Neutral40,
    outlineVariant    = Neutral20,

    inverseSurface    = Neutral80,
    inverseOnSurface  = Neutral10,
    inversePrimary    = Blue40,

    scrim             = Neutral0,
)
