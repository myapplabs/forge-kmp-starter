# AppLabs Design System — Component Inventory

**Module:** `:shared:design`
**Package:** `llc.applabs.design`
**Theme:** Facebook-inspired blue (`#0D68DF` primary; the palette meets WCAG AA text contrast)

Forge must consult this file before writing any UI code. Use only the
components listed here instead of raw Material 3 components. If a component
you need does not exist here, create it in `:shared:design` first, then use it.

---

## Theme

### `AppLabsTheme`
```kotlin
import llc.applabs.design.AppLabsTheme

AppLabsTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
)
```
Wraps every screen. Already applied at the root in `App.kt`. Do not nest it.

---

## Design Tokens

### `AppLabsSpacing`
```kotlin
import llc.applabs.design.tokens.AppLabsSpacing

AppLabsSpacing.xxs   // 2dp
AppLabsSpacing.xs    // 4dp
AppLabsSpacing.sm    // 8dp
AppLabsSpacing.md_sm // 12dp
AppLabsSpacing.md    // 16dp  ← standard content padding
AppLabsSpacing.md_lg // 20dp
AppLabsSpacing.lg    // 24dp  ← section spacing
AppLabsSpacing.xl    // 32dp
AppLabsSpacing.xxl   // 48dp
AppLabsSpacing.xxxl  // 64dp
```
Never use hardcoded `dp` values. Always use a spacing token.

### Shape aliases
```kotlin
import llc.applabs.design.tokens.*

ShapeButton      // RoundedCornerShape(8dp)
ShapeCard        // RoundedCornerShape(12dp)
ShapeChip        // RoundedCornerShape(4dp)
ShapeTextField   // RoundedCornerShape(8dp)
ShapeDialog      // RoundedCornerShape(16dp)
ShapeBottomSheet // top corners 16dp
ShapePill        // RoundedCornerShape(50)
```

### Color palette (direct use when MaterialTheme roles are insufficient)
```kotlin
import llc.applabs.design.tokens.*

Blue40       // #0D68DF — primary blue (AA with white text and as text)
Blue50       // #358CFF — lighter blue for dark mode
SuccessGreen // #30851F (SuccessGreenBright #42B72A: dark-mode tertiary, under black text)
ErrorRed     // #DB050C
Neutral10    // #1C1E21 — dark text
Neutral30    // #65676B — secondary text
Neutral90    // #F0F2F5 — light background
```
Prefer `MaterialTheme.colorScheme.*` roles over direct color references.

---

## Components

### `AppLabsPrimaryButton` — filled, high emphasis
```kotlin
import llc.applabs.design.components.AppLabsPrimaryButton
import llc.applabs.design.components.AppLabsPrimaryButtonFull

AppLabsPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
)

// Full-width convenience wrapper
AppLabsPrimaryButtonFull(text, onClick, modifier, enabled, isLoading)
```
Use for: main CTA on a screen, confirm actions.

### `AppLabsSecondaryButton` — outlined, medium emphasis
```kotlin
import llc.applabs.design.components.AppLabsSecondaryButton

AppLabsSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
)
```
Use for: secondary actions, "Back", "Cancel" when alongside a primary.

### `AppLabsDestructiveButton` — filled red
```kotlin
import llc.applabs.design.components.AppLabsDestructiveButton

AppLabsDestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
)
```
Use for: irreversible actions (delete, revoke, abandon).

### `AppLabsTextButton` — no background, low emphasis
```kotlin
import llc.applabs.design.components.AppLabsTextButton

AppLabsTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
)
```
Use for: inline actions, "Learn more", "Skip".

---

### `AppLabsTextField` — standard text input
```kotlin
import llc.applabs.design.components.AppLabsTextField

AppLabsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    error: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
)
```

### `AppLabsPasswordFieldFull` — password input with show/hide toggle
```kotlin
import llc.applabs.design.components.AppLabsPasswordFieldFull

AppLabsPasswordFieldFull(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
    visibilityOnIcon: ImageVector,   // icon to show when password IS visible
    visibilityOffIcon: ImageVector,  // icon to show when password is hidden
)
```
Use for: all password and secret inputs. Never use a plain TextField for passwords.

---

### `AppLabsCard` — filled card
```kotlin
import llc.applabs.design.components.AppLabsCard

AppLabsCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
)
```

### `AppLabsElevatedCard` — drop shadow card
```kotlin
AppLabsElevatedCard(modifier, onClick, content)
```
Use when the card needs to stand out from the background (e.g., on a gray bg).

### `AppLabsOutlinedCard` — bordered card
```kotlin
AppLabsOutlinedCard(modifier, onClick, content)
```
Use in list items on white backgrounds.

---

### `AppLabsTopBar` — screen top app bar
```kotlin
import llc.applabs.design.components.AppLabsTopBar

AppLabsTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector? = null,
    navigationContentDescription: String? = "Back",
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
)
```
All screens must use this. Never use Material `TopAppBar` directly.

---

### `AppLabsLoadingScreen` — full-screen loading
```kotlin
import llc.applabs.design.components.AppLabsLoadingScreen

AppLabsLoadingScreen(
    modifier: Modifier = Modifier,
    message: String? = null,
)
```

### `AppLabsLoadingIndicator` — inline small spinner
```kotlin
AppLabsLoadingIndicator(modifier: Modifier = Modifier)
```

---

### `AppLabsEmptyState` — empty list / no content
```kotlin
import llc.applabs.design.components.AppLabsEmptyState

AppLabsEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
)
```

### `AppLabsErrorState` — error with retry
```kotlin
import llc.applabs.design.components.AppLabsErrorState

AppLabsErrorState(
    message: String,
    modifier: Modifier = Modifier,
    title: String = "Something went wrong",
    retryLabel: String = "Try Again",
    onRetry: (() -> Unit)? = null,
    icon: ImageVector? = null,
)
```

---

### `AppLabsConfirmDialog` — confirmation dialog
```kotlin
import llc.applabs.design.components.AppLabsConfirmDialog

AppLabsConfirmDialog(
    title: String,
    body: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissLabel: String = "Cancel",
    isDestructive: Boolean = false,
)
```

### `AppLabsInfoDialog` — acknowledgement dialog
```kotlin
import llc.applabs.design.components.AppLabsInfoDialog

AppLabsInfoDialog(
    title: String,
    body: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissLabel: String = "OK",
)
```

---

### `AppLabsBadge` — status pill
```kotlin
import llc.applabs.design.components.AppLabsBadge
import llc.applabs.design.components.AppLabsBadgeStyle

AppLabsBadge(
    text: String,
    modifier: Modifier = Modifier,
    style: AppLabsBadgeStyle = AppLabsBadgeStyle.Primary,
    // AppLabsBadgeStyle.Primary  — blue
    // AppLabsBadgeStyle.Success  — green
    // AppLabsBadgeStyle.Neutral  — gray
    // AppLabsBadgeStyle.Custom   — use customContainerColor + customContentColor
)
```
Use for: Pro badge, Active/Expired status, feature labels.

---

## Rules

1. **Never use raw Material components** when an AppLabs equivalent exists.
   No bare `Button`, `TextField`, `TopAppBar`, `Card`, `AlertDialog`.

2. **Never hardcode dp spacing values.** Use `AppLabsSpacing.*` tokens.

3. **Never hardcode colors.** Use `MaterialTheme.colorScheme.*` roles first,
   named palette tokens (`Blue40`, etc.) only when a role doesn't fit.

4. **New components go in `:shared:design` first**, then get used in `:shared:ui`.
   Do not define one-off composables that duplicate existing components.

5. **Always pass `modifier` through** to the root layout of every component.
