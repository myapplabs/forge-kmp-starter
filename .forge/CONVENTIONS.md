# ForgeStarter Architecture Conventions

The primary reference for AI agents (and people) working on this codebase. Read it before making
changes. ANVIL enforces the rules marked **enforced**; the rest are conventions reviews check.

---

## 1. Modules

```
androidApp/        Android entry point (Application + Activity) — thin
iosApp/            iOS entry point (SwiftUI app hosting the Compose UI) — thin
shared/domain      models, repository interfaces, use cases, AppDependencies
shared/data        repository implementations, data sources, AppContainer
shared/design      the design system: theme, tokens, components (AppLabs default)
shared/ui          screens, ViewModels, navigation, App() — shared by Android and iOS
```

### `:shared:domain` — pure Kotlin

- Domain models (`data class`, `sealed class`, `enum`), repository interfaces, use cases (one
  public `operator fun invoke()` each) and `AppDependencies`.
- **Enforced:** no `android.*`, `androidx.*` or `platform.*` imports; no UI imports. Only
  `kotlin.*` / `kotlinx.*` (coroutines and `Flow` are fine).

### `:shared:data` — data layer

- Repository implementations and data sources (in-memory, files, DataStore, network when a spec
  needs it), and `AppContainer`, the app's object graph.
- **Enforced:** never imports `:shared:ui`.
- Repositories take their data sources as constructor parameters — no static or global access.
- Platform specifics go in `androidMain` / `iosMain` via `expect`/`actual` or an interface the
  entry point implements.

### `:shared:design` — design system

- `AppLabsTheme`, tokens (`AppLabsColors`, `AppLabsTypography`, `AppLabsSpacing`,
  `AppLabsShapes`) and components (`AppLabsPrimaryButton`, `AppLabsCard`, `AppLabsTextField`, …).
- The AppLabs design is the **default**. When the app has a Figma design file, its styles replace
  the token values; components keep working because they only use tokens.
- Inventory and usage: `.forge/DESIGN_COMPONENTS.md` — consult it before writing UI.

### `:shared:ui` — screens

- One package per feature (`ui/<feature>/`): `<Feature>Contract.kt`, `<Feature>ViewModel.kt`,
  `<Feature>Screen.kt`. `App.kt` holds the theme, navigation and ViewModel creation.
- **Enforced:** never imports `:shared:data` in `commonMain` (only `iosMain`'s
  `MainViewController` builds the `AppContainer`).
- Build UI from `:shared:design` components and tokens — not raw Material components, colors or
  `dp` literals. If a component is missing, add it to `:shared:design` first.

### Entry points — `androidApp/`, `iosApp/`

- Create one `AppContainer` and pass it to `App(deps = …)`. No logic here.
- Android: `StarterApp` (holds the container) and `MainActivity`. iOS: `MainViewController()` in
  `shared/ui/src/iosMain`, called from `iosApp/iosApp/ContentView.swift`.

---

## 2. Dependency injection — manual, no framework

`AppDependencies` (domain) lists what the UI needs as interfaces; `AppContainer` (data) creates
the implementations once. `App()` builds each screen's ViewModel from `deps`:

```kotlin
composable(Routes.COUNTER) {
    val viewModel = viewModel {
        CounterViewModel(getCounterUseCase = GetCounterUseCase(deps.counterRepository), …)
    }
    CounterScreen(viewModel = viewModel)
}
```

New repository or service: add the interface in domain, a property to `AppDependencies`, the
implementation in data, and create it in `AppContainer`. Tests pass fakes to constructors.

---

## 3. UiState / UiEvent / UiEffect

```kotlin
data class CounterUiState(val count: Int = 0, val isLoading: Boolean = false, val error: String? = null)
sealed class CounterUiEvent { data object Increment : CounterUiEvent() … }
sealed class CounterUiEffect { data class ShowMessage(val message: String) : CounterUiEffect() }
```

- The ViewModel exposes `StateFlow<UiState>` and a `Channel`-backed `Flow<UiEffect>`, and accepts
  events through `onEvent(event)`. It calls use cases, never repositories.
- Screens collect state and send events. A stateless `<Feature>Content(uiState, onEvent)` is used
  by the screen and by previews and tests.

---

## 4. Adding a feature

1. Domain: model, repository interface, use cases (+ tests with a fake repository).
2. Data: implementation (+ tests), wired in `AppContainer` and `AppDependencies`.
3. UI: contract, ViewModel (+ tests with `StandardTestDispatcher`), screen from design components.
4. Navigation: a route in `Routes` and a `composable(...)` in `App()`.
5. Platform code, if any, in `androidMain` / `iosMain` of the right module.
6. Run `.forge/verify.sh`.

---

## 5. Naming and packages

| Thing | Pattern | Example |
|---|---|---|
| Package | `<app package>.<module>.<feature>` | `com.forge.starter.ui.counter` |
| Use case | `<Verb><Noun>UseCase` | `IncrementCounterUseCase` |
| Repository | `<Noun>Repository` / `<Noun>RepositoryImpl` | `CounterRepositoryImpl` |
| Screen / content | `<Feature>Screen` / `<Feature>Content` | `CounterScreen` |
| ViewModel | `<Feature>ViewModel` | `CounterViewModel` |
| Route | `Routes.<FEATURE>` | `Routes.COUNTER` |

---

## 6. Testing

- Tests live in `commonTest` and run on the JVM through `withHostTest {}` (every module with tests
  enables it) — so they run on Linux, in CI and in FORGE's verification. iOS-only behaviour goes in
  `iosTest` and runs on macOS (the iOS workflow).
- **Enforced** (TEST-008): a module with test sources must actually run tests in `verify.sh`.
- Coroutines: `runTest` + `StandardTestDispatcher`; `Dispatchers.setMain` in `@BeforeTest`,
  `resetMain` in `@AfterTest`. Fakes over mocks.
- Names: backtick sentences — `` `increment event increases count by one` ``.

---

## 7. Dependencies

- All versions in `gradle/libs.versions.toml`; modules use `libs.*` aliases only.
- **Dependency locking:** every module has a `gradle.lockfile`. After adding, removing or
  upgrading a dependency run `./gradlew resolveAndLockAll --write-locks` and commit the lockfiles.
- Forbidden libraries: `.forge/policies/dependency.yaml` (ANVIL DEP-001).
- No networking library by default: add Ktor (or another client) when a spec needs the network.

---

## 8. CI

- `.github/workflows/ci.yml` — every push and pull request (Linux): `.forge/verify.sh`.
- `.github/workflows/ios.yml` — pushes to `master` and on demand (macOS): iOS tests, framework
  link and an unsigned simulator build of `iosApp`. macOS minutes cost 10× on private repos, so it
  does not run on every pull request.
- `.github/workflows/canary.yml` — weekly early warning for the next Linux runner image.
