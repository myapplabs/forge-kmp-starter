# ForgeStarter

A Kotlin Multiplatform (Android + iOS) starting point for apps built with
[Forge by AppLabs](https://github.com/myapplabs/forge). It is a small, working app — a counter —
with the structure, design system, checks and CI that FORGE runs expect, so a new app's first run
builds on a green base.

## Start a new app

In FORGE Desktop: **Projects → New app**. Or in a terminal:

```bash
forge new MyApp --package com.example.myapp
```

`forge new` clones this repository, replaces its placeholders (`.forge/template.yaml`: app name
`ForgeStarter`, package `com.forge.starter`) everywhere — Kotlin, Gradle, Swift, the Xcode project,
plists — renames the package directories, makes the first commit and runs `forge init`.

Then write the product spec (FORGE Desktop → Specs, with CRUCIBLE coaching), plan the backlog and
run tickets.

## What's inside

```
androidApp/        Android entry point (StarterApp + MainActivity)
iosApp/            iOS app (SwiftUI host for the shared Compose UI; Xcode project)
shared/domain      models, repository interfaces, use cases, AppDependencies
shared/data        repository implementations, AppContainer (manual DI)
shared/design      design system — AppLabs default theme, tokens and components
shared/ui          screens, ViewModels, navigation, App() — shared by Android and iOS
.forge/            FORGE: project.yaml, verify.sh, CONVENTIONS.md, DESIGN_COMPONENTS.md, policies, skills
.github/workflows  CI (Linux), iOS (macOS), weekly runner canary
specs/             spec templates and examples
```

- **UI:** Compose Multiplatform; state in ViewModels with `StateFlow` (UiState / UiEvent /
  UiEffect). Navigation Compose.
- **Dependency injection:** manual — `AppContainer` creates implementations once; the UI sees
  only `AppDependencies` interfaces. Works the same on iOS (no Android-only DI framework).
- **No networking library** until a spec needs one.
- Architecture, naming and testing rules: [`.forge/CONVENTIONS.md`](.forge/CONVENTIONS.md).

## Design system

`shared/design` is the **AppLabs default design**: `AppLabsTheme`, tokens (`AppLabsColors`,
`AppLabsTypography`, `AppLabsSpacing`, `AppLabsShapes`) and components (`AppLabsPrimaryButton`,
`AppLabsCard`, `AppLabsTextField`, `AppLabsTopBar`, …), listed in
[`.forge/DESIGN_COMPONENTS.md`](.forge/DESIGN_COMPONENTS.md). Screens use only these.

When the app has its own Figma design file (`figma.file_url` in `.forge/project.yaml`), run
`forge design sync`: the file's color and text styles become `tokens/AppThemeTokens.kt` (generated —
roles the file does not define keep the AppLabs defaults), which `AppLabsTheme` uses. Components
keep working because they only use the theme and tokens. `forge design sync --reset` goes back to the
AppLabs defaults. Name Figma styles after theme roles ("Primary", "On Primary", "Background",
"Text", "Body Large", "Heading 1"…); sync lists anything it could not place.

## Build and test

Prerequisites: JDK 21, the Android SDK (`local.properties` or `ANDROID_HOME`); for iOS, macOS with
Xcode.

```bash
bash .forge/verify.sh          # what FORGE and CI run: Android + iOS compile, all unit tests
./gradlew :androidApp:installDebug
open iosApp/iosApp.xcodeproj   # macOS: run the iosApp target
```

Unit tests live in `commonTest` and also run on the JVM (`withHostTest {}`), so they run on Linux;
`verify.sh` fails if a module has tests but none ran.

## Dependencies

All versions are in `gradle/libs.versions.toml`. Every module has a `gradle.lockfile` (dependency
locking: reproducible builds, and ANVIL's Trivy check sees transitive dependencies). After adding,
removing or upgrading a dependency:

```bash
./gradlew resolveAndLockAll --write-locks   # then commit the lockfiles
```

Forbidden libraries are listed in `.forge/policies/dependency.yaml`.

## CI

| Workflow | Runs | Does |
|---|---|---|
| `ci.yml` | every push and pull request (Ubuntu, pinned) | `.forge/verify.sh` |
| `ios.yml` | pushes to `master`, on demand (macOS, pinned) | iOS unit tests on the simulator, framework link, unsigned simulator build of `iosApp` |
| `canary.yml` | weekly | `verify.sh` on `ubuntu-latest` — early warning before moving the pin |

macOS minutes cost 10× Linux minutes on private repositories, so the iOS workflow does not run on
every pull request. With FORGE's integration on (`integration.mode: direct` in
`.forge/project.yaml`), an approved run is pushed to `master` and done only when both workflows are
green.

## FORGE files

| File | Purpose |
|---|---|
| `.forge/project.yaml` | models, cost limit, Figma file, integration, launch check |
| `.forge/verify.sh` | the verification command (keep `MODULES` in sync with `settings.gradle.kts`) |
| `.forge/CONVENTIONS.md` | architecture guide agents read before writing code |
| `.forge/DESIGN_COMPONENTS.md` | design-system inventory agents must use for UI |
| `.forge/policies/` | deterministic rules ANVIL enforces (architecture, dependencies, conventions) |
| `.forge/skills/` | domain knowledge injected into prompts when relevant |
| `.forge/template.yaml` | placeholders `forge new` replaces |
