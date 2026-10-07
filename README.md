# ForgeStarter

A production-ready Kotlin Multiplatform (KMP) starter template with Forge
AI-assisted development pre-configured.

Build Android and iOS apps from a single shared codebase. Add features by
describing them in plain English — Forge researches, plans, implements, and
verifies the code automatically.

---

## Start here: make it yours

The starter ships with the package `com.forge.starter` and the app name
`ForgeStarter`. **You do not need to rename these manually.** Just tell Forge
in your first spec:

```
Rename the app to "MyApp" and update the package name to "com.mycompany.myapp"
throughout all modules, build files, and manifest.
```

Forge will research every file that references the old package, rename them
consistently, and verify the build still passes. Same for the app name, root
project name in `settings.gradle.kts`, and iOS bundle identifier.

You can also combine it with your first real feature:

```
Rename the app to "Budgetly" (package: com.budgetly.app) and add a home screen
that shows a monthly spending summary with a breakdown by category.
```

Forge handles the rename and the feature in a single run.

---

## Swapping libraries

You can replace any library in the stack — DI framework, networking, persistence,
or anything else — by telling Forge what you want. No manual find-and-replace.

```
Replace Hilt with Koin for dependency injection across all modules.
Update all DI wiring, remove Hilt annotations, and add Koin modules.
```

```
Replace Ktor with Retrofit for networking in shared:data.
```

```
Add SQLDelight for local persistence in shared:data, replacing the in-memory stubs.
```

**How it works:** Forge reads `.forge/CONVENTIONS.md` before every run — that
file is the architecture guide it follows. When you ask Forge to swap a library,
it will migrate the code *and* update `CONVENTIONS.md` so all future runs use
the new convention. You can also edit `CONVENTIONS.md` yourself before running
the spec if you want to be explicit about the new patterns.

The libraries in this starter are defaults, not requirements. Forge adapts to
your stack.

---

## What's inside

| Layer | Technology |
|-------|------------|
| UI | Compose Multiplatform (shared across Android + iOS) |
| State management | ViewModel + StateFlow (UiState / UiEvent / UiEffect) |
| Dependency injection | Hilt (Android) |
| Networking | Ktor |
| AI dev workflow | [Forge](https://github.com/myapplabs/forge) |

---

## Module structure

```
ForgeStarter/
├── app/                    Android entry point — Hilt wiring, NavHost, @HiltViewModel wrappers
├── shared/
│   ├── domain/             Pure Kotlin — models, repository interfaces, use cases
│   ├── data/               KMP data layer — repository impls, Ktor, DTOs
│   └── ui/                 KMP Compose Multiplatform — screens, ViewModels, navigation
├── iosApp/                 iOS entry point (SwiftUI host)
└── .forge/                 Forge configuration — project.yaml, CONVENTIONS.md, policies/
```

### Dependency rules (enforced by Forge/ANVIL)

```
app  →  shared:ui  →  shared:domain
app  →  shared:data  →  shared:domain
         (data and ui never import each other)
```

`shared:domain` has zero platform imports — it compiles for all targets.

---

## Quick start

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17+
- Xcode 15+ (for iOS builds)
- [Forge](https://github.com/myapplabs/forge) installed (`pip install -e path/to/forge`)
- Anthropic API key (for Forge AI features)

### Build and run

```bash
# Android
./gradlew :app:assembleDebug

# Run all tests
./gradlew test
```

For iOS, open `iosApp/iosApp.xcodeproj` in Xcode and run on a simulator.

---

## Adding a new feature with Forge

This is the intended workflow. You describe the feature; Forge implements it.

```bash
# Set your API key
export ANTHROPIC_API_KEY=sk-ant-...

# Check if your spec is clear enough
forge spec check --spec "Add a todo list screen" --project .

# Start the feature run
forge feature --spec "Add a todo list screen with add/delete/complete actions" --project .
```

Forge will:
1. **Research** — read the codebase, map the module structure, find existing patterns
2. **Plan** — produce a step-by-step implementation plan following `CONVENTIONS.md`
3. **Pause for your approval** — you review the plan before any code is written
4. **Execute** — implement in an isolated git worktree
5. **Verify** — run `./gradlew test` and ANVIL deterministic checks
6. **Review** — independent AI review pass
7. **Pause for your approval** — you merge or reject

```bash
# After the plan is ready, inspect and approve
forge inspect plan --project .
forge approve --project .

# After execution, inspect and approve
forge inspect verify --project .
forge inspect review --project .
forge approve --project .
```

---

## The UiState / UiEvent / UiEffect pattern

Every feature follows this contract. The `counter` feature is the reference
implementation.

```kotlin
// Contract — one file per feature
data class CounterUiState(val count: Int = 0, val isLoading: Boolean = false)

sealed class CounterUiEvent {
    data object Increment : CounterUiEvent()
    data object Decrement : CounterUiEvent()
}

sealed class CounterUiEffect {
    data class ShowMessage(val message: String) : CounterUiEffect()
}
```

- **UiState** — what the UI renders; collected as a `StateFlow`
- **UiEvent** — user actions sent to the ViewModel
- **UiEffect** — one-shot side effects (navigation, toasts); emitted via `Channel`

ViewModels live in `:shared:ui` as plain KMP classes (no Hilt). The `:app` module
wraps them in thin `@HiltViewModel` classes for Android DI.

---

## Adding a feature manually

Follow these steps in order. See `.forge/CONVENTIONS.md` for the full guide.

```
[ ] Domain model          shared/domain/src/commonMain/.../domain/model/
[ ] Repository interface  shared/domain/src/commonMain/.../domain/repository/
[ ] Use cases             shared/domain/src/commonMain/.../domain/usecase/
[ ] Repository impl       shared/data/src/commonMain/.../data/repository/
[ ] UiState/Event/Effect  shared/ui/src/commonMain/.../ui/<feature>/<Feature>Contract.kt
[ ] ViewModel             shared/ui/src/commonMain/.../ui/<feature>/<Feature>ViewModel.kt
[ ] Screen + Content      shared/ui/src/commonMain/.../ui/<feature>/<Feature>Screen.kt
[ ] Navigation dest       shared/ui/src/commonMain/.../ui/navigation/NavGraph.kt
[ ] @HiltViewModel wrap   app/src/main/kotlin/.../app/<feature>/<Feature>ViewModelWrapper.kt
[ ] Hilt bindings         app/src/main/kotlin/.../app/di/DataModule.kt + DomainModule.kt
[ ] NavHost entry         app/src/main/kotlin/.../app/MainActivity.kt
[ ] Tests                 use case + repository + ViewModel in commonTest
```

Root package: `com.forge.starter`

---

## Testing

All tests run on JVM — no emulator required.

```bash
./gradlew test
```

| Test location | What's tested |
|---------------|--------------|
| `shared/domain/src/commonTest/` | Use cases |
| `shared/data/src/commonTest/` | Repository implementations |
| `shared/ui/src/commonTest/` | ViewModels |
| `app/src/androidUnitTest/` | Android-specific logic |

Use `Fake<Dependency>` stubs in `commonTest` (not Mockk — Mockk is JVM-only).
Use [Turbine](https://github.com/cashapp/turbine) for `Flow` assertions.

---

## Forge configuration

`.forge/project.yaml` — the configuration Forge reads before each run:

```yaml
name: ForgeStarter
type: kmp
architecture:
  ui: compose-multiplatform
  state: viewmodel-stateflow
  di: hilt
  networking: ktor
source_sets:
  common: commonMain
  android: androidMain
  ios: iosMain
verification:
  command: "./forge/verify.sh"
  timeout_seconds: 300
allowed_commands:
  - "./gradlew"
  - "git"
autonomy_level: 1
```

`.forge/CONVENTIONS.md` — the architecture guide injected into every Forge
prompt. Keep this file up to date as the project evolves. Forge follows it
exactly.

`.forge/policies/` — ANVIL policy YAML files. Custom deterministic rules
enforced on every verification run.

---

## Dependencies

All versions are managed in `gradle/libs.versions.toml`. Never hardcode versions
in build files.

To add a dependency:

```toml
# gradle/libs.versions.toml
[versions]
my-lib = "1.2.3"

[libraries]
my-lib = { module = "com.example:my-lib", version.ref = "my-lib" }
```

```kotlin
// build.gradle.kts
implementation(libs.my.lib)
```

---

## Documentation

| File | Contents |
|------|----------|
| `.forge/CONVENTIONS.md` | Full architecture guide — the single source of truth for how code is structured |
| `.forge/project.yaml` | Forge project configuration |
| `.forge/policies/architecture.yaml` | ANVIL architecture enforcement rules |
