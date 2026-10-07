# ForgeStarter Architecture Conventions

This document is the primary reference for AI agents working on this codebase.
Every architectural decision is documented here. Read this before making any changes.

---

## Table of Contents

1. [Module Responsibilities](#1-module-responsibilities)
2. [The UiState / UiEvent / UiEffect Pattern](#2-the-uistate--uievent--uieffect-pattern)
3. [How to Add a New Feature](#3-how-to-add-a-new-feature)
4. [Dependency Injection (Hilt)](#4-dependency-injection-hilt)
5. [Naming Conventions](#5-naming-conventions)
6. [Where to Put New Composables](#6-where-to-put-new-composables)
7. [How to Add a New Navigation Destination](#7-how-to-add-a-new-navigation-destination)
8. [Package Naming Convention](#8-package-naming-convention)
9. [Testing Conventions](#9-testing-conventions)
10. [Dependency Version Management](#10-dependency-version-management)

---

## 1. Module Responsibilities

### `:shared:domain` — Pure Kotlin, zero platform imports

**Contains:**
- Domain models (`data class`, `sealed class`, `enum`)
- Repository interfaces (e.g., `interface CounterRepository`)
- Use case classes (one public `operator fun invoke()` method each)

**Rules (enforced — never violate these):**
- NO `android.*` imports
- NO `androidx.*` imports
- NO `platform.*` imports (no iOS/Apple frameworks)
- NO UI framework imports
- ONLY `kotlin.*` and `kotlinx.*` standard library imports are allowed
- Coroutines are allowed for `suspend` functions and `Flow` in interfaces

**Reason:** This module must compile for all targets — Android, iOS, JVM, JS.
Any platform leak breaks cross-platform compilation.

---

### `:shared:data` — KMP data layer

**Contains:**
- Repository implementations (e.g., `CounterRepositoryImpl`)
- Remote data source interfaces and Ktor-based implementations
- Local data source interfaces and in-memory/stub implementations
- DTO models annotated with `@Serializable`
- Mapper functions (`DTO -> Domain model`)

**Source sets:** `commonMain`, `androidMain`, `iosMain`

**Rules:**
- MUST NOT import anything from `:shared:ui`
- Repository implementations take data sources as constructor parameters — no static access
- Platform-specific Ktor engines go in `androidMain`/`iosMain` via `expect/actual`
- DTOs live in `data.remote.dto` package; mappers in `data.mapper` package

**Allowed imports from other modules:** `:shared:domain` only

---

### `:shared:ui` — KMP Compose Multiplatform UI

**Contains:**
- Shared `@Composable` components (design system, screens)
- Shared `ViewModel` classes using `StateFlow`
- `UiState`, `UiEvent`, `UiEffect` contracts per feature
- Navigation destination definitions (`Destination` sealed class)

**Source sets:** `commonMain`, `androidMain`, `iosMain`

**Rules:**
- MUST NOT import anything from `:shared:data`
- All data access goes through `:shared:domain` repository interfaces
- Use cases and repository interfaces are injected into ViewModels via constructor
- No Hilt annotations — ViewModels are plain classes with constructor injection
- `commonMain` Composables use Compose Multiplatform only — no `androidx.compose.*` directly

**Allowed imports from other modules:** `:shared:domain` only

---

### `:app` — Android application entry point

**Contains:**
- `ForgeStarterApp` (Hilt `@HiltAndroidApp` application class)
- `MainActivity` (NavHost setup, nothing else)
- Hilt `@HiltViewModel` wrapper classes per feature (thin — no business logic)
- Hilt `@Module` classes wiring everything together
- No business logic — ever

**Rules:**
- This is the ONLY module that may import from ALL other modules
- This is the ONLY module that may use Hilt annotations (`@HiltAndroidApp`, `@HiltViewModel`, `@Module`, `@Provides`)
- Shared modules must NOT depend on Hilt
- All DI binding lives here

---

## 2. The UiState / UiEvent / UiEffect Pattern

Every feature has a contract file named `<Feature>Contract.kt` defining three types:

### UiState — what the UI renders

```kotlin
data class CounterUiState(
    val count: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

- Immutable data class
- Has sensible defaults — UI can render safely with the initial state
- Represents a complete snapshot (not deltas)

### UiEvent — user actions sent to the ViewModel

```kotlin
sealed class CounterUiEvent {
    data object Increment : CounterUiEvent()
    data object Decrement : CounterUiEvent()
    data object Reset : CounterUiEvent()
}
```

- Sealed class — exhaustive when expressions
- Represents intent, not implementation
- `data object` for events with no payload; `data class` for events with payload

### UiEffect — one-shot side effects

```kotlin
sealed class CounterUiEffect {
    data class ShowMessage(val message: String) : CounterUiEffect()
}
```

- Emitted via a `Channel` (not `StateFlow`) — consumed exactly once
- Use for: navigation, toasts, snackbars, permission requests
- Never use for rendering state — use `UiState` for that

### ViewModel wiring

```kotlin
class CounterViewModel(
    private val getCounterUseCase: GetCounterUseCase,
    // ... other use cases
) : ViewModel() {

    private val _uiState = MutableStateFlow(CounterUiState())
    val uiState: StateFlow<CounterUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<CounterUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    fun onEvent(event: CounterUiEvent) { /* dispatch to handlers */ }
}
```

### Composable wiring

```kotlin
@Composable
fun CounterContent(
    uiState: CounterUiState,
    onEvent: (CounterUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    // Renders uiState; calls onEvent on user interactions
}
```

The stateless `Content` composable is the testable unit. The stateful `Screen` composable
collects state from the ViewModel and passes it down.

---

## 3. How to Add a New Feature

Follow these exact steps, in order. The counter feature is the reference implementation.

### Step 1: Domain model in `:shared:domain`

File: `shared/domain/src/commonMain/kotlin/com/forge/starter/domain/model/<Feature>Model.kt`

```kotlin
package com.forge.starter.domain.model

data class UserModel(
    val id: String,
    val name: String,
    val email: String
)
```

### Step 2: Repository interface in `:shared:domain`

File: `shared/domain/src/commonMain/kotlin/com/forge/starter/domain/repository/<Feature>Repository.kt`

```kotlin
package com.forge.starter.domain.repository

interface UserRepository {
    suspend fun getUser(id: String): UserModel
    fun observeUsers(): Flow<List<UserModel>>
}
```

### Step 3: Use cases in `:shared:domain`

One file per use case:
`shared/domain/src/commonMain/kotlin/com/forge/starter/domain/usecase/Get<Feature>UseCase.kt`

```kotlin
class GetUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(id: String): UserModel = userRepository.getUser(id)
}
```

### Step 4: Repository implementation in `:shared:data`

File: `shared/data/src/commonMain/kotlin/com/forge/starter/data/repository/<Feature>RepositoryImpl.kt`

```kotlin
class UserRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource
) : UserRepository {
    override suspend fun getUser(id: String): UserModel =
        remoteDataSource.fetchUser(id).toDomain()
}
```

Note: Constructor-inject all data sources. No static access.

### Step 5: Contract in `:shared:ui`

File: `shared/ui/src/commonMain/kotlin/com/forge/starter/ui/<feature>/<Feature>Contract.kt`

Define `<Feature>UiState`, `<Feature>UiEvent`, `<Feature>UiEffect`.

### Step 6: ViewModel in `:shared:ui`

File: `shared/ui/src/commonMain/kotlin/com/forge/starter/ui/<feature>/<Feature>ViewModel.kt`

Extend `ViewModel` (from `androidx.lifecycle:lifecycle-viewmodel`). Constructor-inject use cases.

### Step 7: Composable screen in `:shared:ui`

File: `shared/ui/src/commonMain/kotlin/com/forge/starter/ui/<feature>/<Feature>Screen.kt`

Define both `<Feature>Screen` (stateful, takes ViewModel) and `<Feature>Content` (stateless, takes state + onEvent).

### Step 8: Navigation destination

In `shared/ui/src/commonMain/kotlin/com/forge/starter/ui/navigation/NavGraph.kt`, add:

```kotlin
data object UserDetail : Destination("user/{userId}") {
    fun createRoute(userId: String) = "user/$userId"
}
```

### Step 9: Hilt wiring in `:app`

**a) `@HiltViewModel` wrapper** in `app/src/main/kotlin/com/forge/starter/app/<feature>/<Feature>ViewModelWrapper.kt`:

```kotlin
@HiltViewModel
class UserViewModelWrapper @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() { /* delegate to shared logic */ }
```

**b) Repository binding** in `app/src/main/kotlin/com/forge/starter/app/di/DataModule.kt`:

```kotlin
@Provides @Singleton
fun provideUserRepository(/* deps */): UserRepository = UserRepositoryImpl(...)
```

**c) Use case binding** in `app/src/main/kotlin/com/forge/starter/app/di/DomainModule.kt`:

```kotlin
@Provides @ViewModelScoped
fun provideGetUserUseCase(repo: UserRepository): GetUserUseCase = GetUserUseCase(repo)
```

### Step 10: Wire into NavHost in `MainActivity`

```kotlin
composable(route = Destination.UserDetail.route) { backStackEntry ->
    val viewModel: UserViewModelWrapper = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    UserContent(uiState = uiState, onEvent = viewModel::onEvent)
}
```

### Step 11: Write tests

See [Testing Conventions](#9-testing-conventions).

---

## 4. Dependency Injection (Hilt)

### Location of all DI code

All Hilt modules live in `:app` at `app/src/main/kotlin/com/forge/starter/app/di/`.

| File | What it provides |
|------|-----------------|
| `DataModule.kt` | Repository implementations (Singleton scope) |
| `DomainModule.kt` | Use case instances (ViewModelScoped) |

### Scoping rules

| Component type | Hilt scope | Lifecycle |
|---------------|------------|-----------|
| Repository | `@Singleton` | App lifetime |
| Use case | `@ViewModelScoped` | ViewModel lifetime |
| ViewModel | `@HiltViewModel` | ViewModel lifecycle |

### The @HiltViewModel pattern for KMP ViewModels

Because ViewModels in `:shared:ui` are plain KMP classes (no Hilt), we create
thin `@HiltViewModel` wrappers in `:app`. The wrapper:
1. Has `@HiltViewModel` and `@Inject constructor`
2. Receives use cases via constructor injection
3. Contains the same state logic as the shared ViewModel (or delegates to it)
4. Is instantiated via `hiltViewModel()` in composables

This keeps shared modules Hilt-free while still using Hilt for Android DI.

---

## 5. Naming Conventions

| Type | Suffix | Example |
|------|--------|---------|
| Domain model | `Model` | `CounterModel`, `UserModel` |
| Repository interface | `Repository` | `CounterRepository` |
| Repository implementation | `RepositoryImpl` | `CounterRepositoryImpl` |
| Use case | `UseCase` | `GetCounterUseCase`, `IncrementCounterUseCase` |
| ViewModel (shared) | `ViewModel` | `CounterViewModel` |
| ViewModel (Hilt wrapper) | `ViewModelWrapper` | `CounterViewModelWrapper` |
| UI contract sealed class | `UiState` / `UiEvent` / `UiEffect` | `CounterUiState` |
| Composable screen | `Screen` (stateful) / `Content` (stateless) | `CounterScreen`, `CounterContent` |
| Hilt module | `Module` | `DataModule`, `DomainModule` |
| Remote data source | `RemoteDataSource` | `CounterRemoteDataSource` |
| Local data source | `LocalDataSource` | `CounterLocalDataSource` |
| DTO | `Dto` | `CounterDto` |

---

## 6. Where to Put New Composables

### Feature screens
`shared/ui/src/commonMain/kotlin/com/forge/starter/ui/<feature>/`

Example: `shared/ui/src/commonMain/kotlin/com/forge/starter/ui/counter/CounterScreen.kt`

### Design system / shared components
`shared/ui/src/commonMain/kotlin/com/forge/starter/ui/components/`

Example: `shared/ui/src/commonMain/kotlin/com/forge/starter/ui/components/ForgeButton.kt`

### Theme
`shared/ui/src/commonMain/kotlin/com/forge/starter/ui/theme/`

### Rules for Composables:
- Every screen has a stateless `Content` composable that accepts `UiState` and `onEvent`
- Every screen has a stateful `Screen` composable that takes a ViewModel (or wrapper)
- Composables must NOT call use cases directly — only through the ViewModel
- Use `modifier: Modifier = Modifier` as the last parameter on all composables
- No hardcoded colors — always use `MaterialTheme.colorScheme.*`
- No hardcoded strings — use string resources or string constants

---

## 7. How to Add a New Navigation Destination

### Step 1: Add to Destination sealed class

In `shared/ui/src/commonMain/kotlin/com/forge/starter/ui/navigation/NavGraph.kt`:

```kotlin
sealed class Destination(val route: String) {
    data object Counter : Destination("counter")
    // Add new destinations here:
    data object UserList : Destination("user_list")
    data object UserDetail : Destination("user/{userId}") {
        fun createRoute(userId: String) = "user/$userId"
        const val ARG_USER_ID = "userId"
    }
}
```

### Step 2: Add composable to NavHost in MainActivity

```kotlin
composable(
    route = Destination.UserDetail.route,
    arguments = listOf(navArgument(Destination.UserDetail.ARG_USER_ID) {
        type = NavType.StringType
    })
) { backStackEntry ->
    val userId = backStackEntry.arguments?.getString(Destination.UserDetail.ARG_USER_ID) ?: return@composable
    val viewModel: UserViewModelWrapper = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    UserContent(uiState = uiState, onEvent = viewModel::onEvent)
}
```

### Step 3: Navigate to the destination

Pass `navController` or a `() -> Unit` lambda down to composables that need to navigate.
Never pass `NavController` into ViewModels — emit a `UiEffect` instead.

```kotlin
// In the parent composable
LaunchedEffect(Unit) {
    viewModel.uiEffect.collectLatest { effect ->
        when (effect) {
            is CounterUiEffect.NavigateToDetail -> navController.navigate(
                Destination.UserDetail.createRoute(effect.userId)
            )
        }
    }
}
```

---

## 8. Package Naming Convention

Root package: `com.forge.starter`

Pattern: `com.forge.starter.<module>.<feature>`

| Module | Feature | Full package |
|--------|---------|-------------|
| `shared:domain` | model | `com.forge.starter.domain.model` |
| `shared:domain` | repository | `com.forge.starter.domain.repository` |
| `shared:domain` | usecase | `com.forge.starter.domain.usecase` |
| `shared:data` | repository | `com.forge.starter.data.repository` |
| `shared:data` | remote | `com.forge.starter.data.remote` |
| `shared:data` | local | `com.forge.starter.data.local` |
| `shared:data` | dto | `com.forge.starter.data.remote.dto` |
| `shared:ui` | counter | `com.forge.starter.ui.counter` |
| `shared:ui` | theme | `com.forge.starter.ui.theme` |
| `shared:ui` | navigation | `com.forge.starter.ui.navigation` |
| `shared:ui` | components | `com.forge.starter.ui.components` |
| `app` | root | `com.forge.starter.app` |
| `app` | di | `com.forge.starter.app.di` |
| `app` | feature wrappers | `com.forge.starter.app.<feature>` |

---

## 9. Testing Conventions

### Test location rules

| Test type | Location | Source set |
|-----------|----------|-----------|
| Domain use case tests | `:shared:domain` | `commonTest` |
| Repository impl tests | `:shared:data` | `commonTest` |
| ViewModel tests | `:shared:ui` | `commonTest` |
| Android-specific tests | `:app` | `androidUnitTest` |

**No instrumented tests** (`androidInstrumentedTest`). All tests run on JVM.

### Fake/stub pattern

Create `Fake<Dependency>` classes in `commonTest`. Never use Mockk in `commonTest`
(Mockk is Android/JVM only). Use `FakeCounterRepository` as reference.

### ViewModel test pattern

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class CounterViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `increment increases count`() = runTest {
        val viewModel = CounterViewModel(/* fakes */)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            awaitItem() // consume initial state

            viewModel.onEvent(CounterUiEvent.Increment)
            testDispatcher.scheduler.advanceUntilIdle()

            val updated = awaitItem()
            assertEquals(1, updated.count)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

Key rules:
- Always use `StandardTestDispatcher` + `advanceUntilIdle()` (never `UnconfinedTestDispatcher` for production code)
- Use Turbine's `.test {}` extension for `Flow` assertions
- Use `skipItems(n)` to skip intermediate states you don't care about
- `cancelAndIgnoreRemainingEvents()` to end Turbine collection without error

### Minimum test count per feature

Every feature must have at minimum:
- 2 use case tests (get + mutate)
- 3 repository impl tests (initial state, one mutation, persistence)
- 4 ViewModel tests (initial state, increment, decrement, effect emission)

---

## 10. Dependency Version Management

All dependency versions are in `gradle/libs.versions.toml`. Zero hardcoded versions in build files.

### Adding a new dependency

1. Add version to `[versions]` section: `my-lib = "1.2.3"`
2. Add library to `[libraries]` section: `my-lib = { module = "com.example:my-lib", version.ref = "my-lib" }`
3. Reference in build file: `implementation(libs.my.lib)` (dots replace hyphens)
4. For plugins, add to `[plugins]` and reference with `alias(libs.plugins.my.plugin)`

### Updating a dependency

Change only the version in `libs.versions.toml`. Never change the build files.

---

## Summary: The One-Page Cheat Sheet

```
New feature checklist:
[ ] Domain model in shared:domain/model/
[ ] Repository interface in shared:domain/repository/
[ ] Use cases in shared:domain/usecase/ (one per operation)
[ ] Repository impl in shared:data/repository/
[ ] UiState/UiEvent/UiEffect contract in shared:ui/<feature>/
[ ] ViewModel in shared:ui/<feature>/
[ ] Composables (Screen + Content) in shared:ui/<feature>/
[ ] Navigation destination in shared:ui/navigation/NavGraph.kt
[ ] @HiltViewModel wrapper in app/<feature>/
[ ] Hilt bindings in app/di/DataModule.kt + DomainModule.kt
[ ] NavHost composable in app/MainActivity.kt
[ ] Tests: use case + repository + ViewModel
```
