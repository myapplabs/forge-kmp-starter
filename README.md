# ForgeStarter

A production-ready Kotlin Multiplatform (KMP) starter template with Forge
AI-assisted development pre-configured.

Build Android and iOS apps from a single shared codebase. Add features by
describing them in plain English — Forge researches, plans, implements, and
verifies the code automatically.

---

## Start here: make it yours

Two spec templates are included in [`specs/`](specs/):

| Template | Use when |
|----------|----------|
| [`specs/new-app.md`](specs/new-app.md) | Starting a brand new app — covers app identity, all initial screens, navigation, and acceptance criteria in one run |
| [`specs/feature.md`](specs/feature.md) | Adding a feature to an existing app |

**If this is a new app, start with `specs/new-app.md`.** It includes app name
and package renaming alongside your initial screens — Forge handles everything
in a single run. The starter ships with the package `com.forge.starter` and app
name `ForgeStarter`; you do not need to rename anything manually.

```bash
# Copy, fill in, and run
cp specs/new-app.md specs/my-app.md
# edit specs/my-app.md ...
forge feature --spec specs/my-app.md --project .
```

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

## Building with Forge

### Single feature

Copy [`specs/feature.md`](specs/feature.md), fill it in, and run:

```bash
export ANTHROPIC_API_KEY=sk-ant-...

# Optional: estimate API cost before committing
forge estimate --spec specs/my-feature.md --project .

# Check the spec is clear enough
forge spec check --spec specs/my-feature.md --project .

# Run
forge feature --spec specs/my-feature.md --project .
```

Forge will research, plan, pause for your approval, implement, verify, review,
and pause again before merging.

```bash
# Approve the plan (before any code is written)
forge inspect plan --project .
forge approve --project .

# Approve the final result
forge inspect verify --project .
forge inspect review --project .
forge approve --project .
```

### Building a full app across multiple runs

A complete app is too large for a single Forge run. Break it into focused runs —
each one builds on the last, stays reviewable, and keeps the repair loop short.

A typical sequence for a new app:

| Run | Spec | What it does |
|-----|------|-------------|
| Run 0 | `specs/my-app.md` | Rename, module structure, navigation skeleton, design system, error model |
| Run 1 | `specs/run1-auth.md` | Authentication screens and logic |
| Run 2 | `specs/run2-home.md` | Home screen and data layer |
| Run 3 | `specs/run3-settings.md` | Settings, persistence |
| ... | ... | One feature or layer per run |

Keep each run scoped to one concern. Include a **"What NOT to do"** section in
every spec to prevent Forge from reaching into the next run's scope.

Name your spec files to match the run sequence — `specs/run0-foundation.md`,
`specs/run1-auth.md`, etc. Commit them alongside your code so the full history
of what was built and why stays in the repository.

### When a run needs attention

```bash
# See where the run is
forge status --project .

# If verification failed and auto-repair is exhausted
forge repair --project .

# If the run is stuck and you want to start over
forge abandon --project .
```

---

## The UiState / UiEvent / UiEffect pattern

Every feature follows this contract. **The `counter` feature is the reference
implementation** — read it before adding your first feature. It shows the
complete vertical slice: domain model → use cases → repository → ViewModel →
screen → Hilt wiring → tests.

Reference files:
- `shared/domain/src/commonMain/.../domain/model/CounterModel.kt`
- `shared/domain/src/commonMain/.../domain/usecase/`
- `shared/ui/src/commonMain/.../ui/counter/CounterContract.kt`
- `shared/ui/src/commonMain/.../ui/counter/CounterViewModel.kt`
- `shared/ui/src/commonMain/.../ui/counter/CounterScreen.kt`
- `shared/ui/src/commonTest/.../ui/counter/CounterViewModelTest.kt`

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

## Design system

The starter ships with a minimal Material3 theme (`shared/ui/.../theme/Theme.kt`)
— default colors, no custom typography, no branded components. **Forge will use
these defaults unless you tell it otherwise.**

### Bringing your own design

Describe your design in your spec and Forge will implement it. The more specific
you are, the closer the result will be to your Figma:

```
Design tokens:
- Primary: #6750A4
- On-primary: #FFFFFF
- Background: #FFFBFE
- Surface: #FFFBFE
- Error: #B3261E

Typography:
- Display: Roboto 57sp, weight 400
- Headline: Roboto 32sp, weight 400
- Body: Roboto 16sp, weight 400
- Label: Roboto 11sp, weight 500
```

You can also describe component styles directly:

```
Primary button: filled, rounded corners (50% radius), 48dp height, no elevation
Card: 12dp corner radius, 1dp stroke using outline color, no elevation
Top bar: no shadow, background matches surface color
```

### Figma

Forge can read your Figma file directly via the Figma REST API. When configured,
it fetches your color styles, text styles, and component names and injects them
into every run — so the AI knows your exact tokens before writing UI code.

**Setup:**

1. Add to `.forge/project.yaml`:

```yaml
figma:
  file_url: "https://www.figma.com/file/XXXXXXXXXXXXXXXXXXXXXXXX/MyApp"
```

2. Set your Figma API token in the same shell where you run Forge — alongside
   your `ANTHROPIC_API_KEY`. Add both to your shell profile (`~/.zshrc` or
   `~/.bashrc`) so they are available in every session:

```bash
export ANTHROPIC_API_KEY=sk-ant-...
export FIGMA_API_KEY=figd_...
```

Forge reads the token from the environment — it is never stored in project files.

**What Forge fetches:**
- Color styles (solid fills → hex values)
- Text styles (font family, size, weight, line height)
- Component names (for naming Compose components consistently)

If `FIGMA_API_KEY` is absent or the request fails, Forge continues normally.
No run is ever blocked by a Figma fetch failure.

**No Figma file?** Omit the `figma:` block entirely. You can still describe
your design tokens in your spec or in a custom skill at
`.forge/skills/design.yaml` — a skill is the right place for tokens that
should apply to every run, not just one feature.

### Scaffolding a design system

To generate a design system foundation with component stubs:

```bash
forge design scaffold --package com.myapp --module :shared:ui --project .
```

This creates `DesignTokens.kt`, `AppButton.kt`, `AppTextField.kt`,
`AppCard.kt`, `AppTopBar.kt`, and other shared components in `:shared:ui`.
Fill in your token values, then reference the scaffold in your spec so Forge
uses your components instead of inline Material3 calls.

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

## Customizing what Forge knows about your project

Forge is only as good as the context you give it. Three files control what Forge
knows before it writes a single line of code:

### `.forge/CONVENTIONS.md` — your architecture guide

This is the most important file. Forge reads it at the start of every run and
follows it exactly. The starter ships with KMP/Compose/Hilt conventions — but
as your project evolves, this file must evolve with it.

**Update it whenever you:**
- Establish a new pattern (e.g., how you handle errors, how you structure API calls)
- Make an architectural decision that future features should follow
- Swap a library (Forge will update it automatically when you ask it to migrate)

If `CONVENTIONS.md` is out of date, Forge will follow the old pattern. If it is
missing a pattern, Forge will invent one. Keep it current.

### `.forge/skills/` — domain knowledge injected into prompts

Skills are structured guides that give Forge deep knowledge about a specific
technology or pattern.

**Important:** if you do not set `skills:` in `project.yaml`, all built-in
skills are active automatically. You only need to list skills explicitly when
adding custom ones or restricting which built-ins are active.

Built-in skills (always available):

| Skill | What it covers |
|-------|---------------|
| `kmp-core` | KMP architecture, expect/actual, shared module layout |
| `android-jetpack` | Jetpack Compose, ViewModel, Navigation |
| `ios-swiftui` | SwiftUI host, KMP framework integration |
| `kmp-testing` | commonTest, Turbine, Fake stubs |
| `kotlin-style` | Kotlin idioms and style conventions |

```bash
forge skill list               # see all available skills
forge skill show kmp-core      # read what Forge knows about KMP
```

#### Adding a custom skill

Create a YAML file in `.forge/skills/`. The starter includes a template at
`.forge/skills/api-conventions.yaml` — fill it in with your backend contract:

```yaml
name: "api-conventions"
version: "1.0.0"
display_name: "API Conventions"
description: "Backend API patterns and error handling for this project"
phases: [research, plan, execute, review, repair]
content: |
  ## API Conventions
  - Base URL: https://api.myapp.com/v1
  - Auth: Bearer token in Authorization header
  - Errors: sealed class ApiError — never throw raw exceptions
  ...
```

Then validate and reference it:

```bash
forge skill validate .forge/skills/api-conventions.yaml
```

```yaml
# .forge/project.yaml
skills:
  - kmp-core
  - android-jetpack
  - kmp-testing
  - api-conventions    # your custom skill
```

Good candidates for custom skills: your backend API contract, your design
system component inventory, your team's naming conventions, third-party SDK
patterns. The more accurately skills reflect your project, the better every
Forge run will be.

### `.forge/policies/` — deterministic rules ANVIL enforces

Policies are rules that ANVIL checks on every verification run — independently
of any AI judgment. The starter ships with two policy files:

**`architecture.yaml`** — module boundary enforcement (hard gates):
- `ARCH-001`: No `android.*` / `androidx.*` imports in `commonMain` Kotlin files
- `ARCH-002`: `shared/ui` must not import the data layer (`.data.`, `.repository.`, `.dao.`) directly
- `ARCH-003`: `shared/domain` must not import any platform code

**`dependency.yaml`** — forbidden library enforcement (hard gate):
- `DEP-001`: Blocks RxJava, Retrofit, OkHttp direct usage, and LiveData — libraries that conflict with the starter's chosen stack (Coroutines/Flow, Ktor, StateFlow)

ANVIL also runs built-in security checks on every run regardless of policy files:
hardcoded secrets, known-vulnerable CVEs, PII in logs, cleartext HTTP, and
`verify.sh` tamper detection.

Add your own policies for project-specific invariants — for example:
- All network calls must go through a specific interface
- Certain internal packages must never import from each other

---

**The default setup is a starting point, not a constraint.** The more accurately
these files reflect your project, the better every Forge run will be.

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
  command: "./.forge/verify.sh"
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

## Cost

Forge uses your Anthropic API key directly — there is no separate subscription.
Rough ballpark using Claude Sonnet:

| Run type | Typical cost |
|----------|-------------|
| Simple feature (1-2 screens, existing patterns) | $0.05 – $0.20 |
| Medium feature (new data layer + UI) | $0.20 – $0.60 |
| New app foundation (rename, modules, navigation, design system) | $0.50 – $1.50 |
| Full app across multiple runs | $2.00 – $8.00 |

Check before running:

```bash
forge estimate --spec specs/my-feature.md --project .
```

---

## Troubleshooting

**`ANTHROPIC_API_KEY` not set**
```
Error: provider initialization failed
```
Run `export ANTHROPIC_API_KEY=sk-ant-...` before any `forge` command.

**Gradle sync fails after cloning**
Open the project in Android Studio and let it sync. Run `./gradlew assembleDebug` to confirm the build is clean before running Forge.

**iOS build fails — framework not found**
Run `./gradlew :shared:ui:assembleReleaseXCFramework` to generate the KMP framework, then clean and rebuild in Xcode.

**Forge can't find the verification script**
Check `.forge/project.yaml` — `verification.command` must be `./.forge/verify.sh` (note the leading dot).

**Forge run stuck at `NEEDS_INTERVENTION`**
```bash
forge status --project .          # see what failed
forge inspect verify --project .  # read the ANVIL and test output
forge repair --project .          # attempt another repair pass
forge abandon --project .         # give up and start fresh
```

**Plan looks wrong — Forge misunderstood the spec**
Reject the plan with a reason: `forge reject --project .`
Then revise your spec (add more detail to the screens section or tighten the "What NOT to do" list) and re-run.

---

## Documentation

| File | Contents |
|------|----------|
| `.forge/CONVENTIONS.md` | Architecture guide — the single source of truth Forge follows |
| `.forge/project.yaml` | Forge project configuration |
| `.forge/policies/architecture.yaml` | ANVIL architecture enforcement rules |
| `specs/new-app.md` | Spec template for starting a brand new app |
| `specs/feature.md` | Spec template for adding a feature |
| `specs/examples/` | Completed example specs showing what good looks like |
