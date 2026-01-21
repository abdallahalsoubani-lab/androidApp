# Android Project Template

A production-ready Android project template following Google's best practices, modern architecture patterns, and clean code principles.

## 🏗️ Project Structure

```
android-template/
├── app/                          # Main application module
├── build-logic/                  # Convention plugins for Gradle
│   └── convention/
├── core/
│   ├── ui/                       # Design system, theme, components
│   ├── network/                  # Retrofit, OkHttp, interceptors
│   ├── database/                 # Room database
│   ├── datastore/                # DataStore preferences
│   ├── common/                   # Result wrappers, utils, dispatchers
│   ├── analytics/                # Analytics abstraction
│   └── testing/                  # Test utilities and fakes
├── feature/
│   ├── auth/                     # Authentication feature
│   └── template/                 # Sample feature with CRUD operations
├── docs/
│   ├── ARCHITECTURE.md
│   ├── SETUP.md
│   └── FEATURE_TEMPLATE.md
└── gradle/
    └── libs.versions.toml        # Version catalog
```

## 🛠️ Technical Stack

### Build & Dependencies
- **Kotlin DSL** for Gradle configuration
- **Version Catalog** for centralized dependency management
- **Convention Plugins** for DRY build scripts
- **compileSdk**: 34
- **minSdk**: 24
- **targetSdk**: 34

### Architecture Patterns
- **Clean Architecture**: Separation of concerns (presentation, domain, data)
- **MVVM** with **Jetpack Compose**
- **Repository Pattern** for data access
- **Dependency Injection** with Hilt
- **Unidirectional Data Flow (UDF)** in ViewModels

### UI & Compose
- **Material 3** with light/dark theme support
- **Jetpack Compose** for declarative UI
- **Navigation Compose** for screen routing
- **RTL support** ready

### Networking
- **Retrofit 2** for API calls
- **OkHttp** with interceptors (Auth, Logging, Headers)
- **Kotlinx Serialization** for JSON parsing
- **Unified error handling** across the app

### Local Storage
- **Room Database** for persistent local storage
- **DataStore Preferences** for user settings
- **Migration strategy** template

### State Management
- **StateFlow** for observable state
- **SharedFlow** for side effects
- **Result sealed class** for operation outcomes

### Testing
- **JUnit 4** for unit tests
- **Mockk** for mocking
- **Truth** for assertions
- **Turbine** for Flow testing
- **Test Dispatchers** for coroutine testing

### Analytics
- **Abstracted Analytics Tracker** interface
- **Debug implementation** for development
- **Firebase Analytics** integration ready

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Git

### Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd android-template
   ```

2. **Open in Android Studio**
   - File → Open → Select project root
   - Wait for Gradle sync

3. **Run the app**
   - Click Run (or press Shift + F10)
   - Select an emulator or connected device

### Building

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Run UI tests
./gradlew connectedAndroidTest

# Code quality checks
./gradlew detekt
./gradlew ktlint
```

## 📝 Architecture Details

### Unidirectional Data Flow (UDF)

Each screen follows the UDF pattern with three key components:

```kotlin
// State: Immutable data class
data class ScreenUiState(
    val isLoading: Boolean = false,
    val data: List<Item> = emptyList(),
    val error: String? = null
)

// Events: User intents
sealed class ScreenUiEvent {
    data class ItemClicked(val id: String) : ScreenUiEvent()
    object RefreshRequested : ScreenUiEvent()
}

// Effects: One-shot events
sealed class ScreenUiEffect {
    data class ShowToast(val message: String) : ScreenUiEffect()
    data class NavigateTo(val route: String) : ScreenUiEffect()
}
```

### Repository Pattern

```kotlin
interface ItemRepository {
    fun getItems(): Flow<Result<List<Item>>>
    suspend fun getItem(id: String): Result<Item>
    suspend fun saveItem(item: Item): Result<Unit>
    suspend fun deleteItem(id: String): Result<Unit>
}
```

### Clean Architecture Layers

- **Presentation Layer**: ViewModels, Screens, State management
- **Domain Layer**: Use cases, Models, Repository interfaces (pure Kotlin, no Android)
- **Data Layer**: Repository implementations, DataSources, Mappers

## 🔌 Adding a New Feature

1. Create a new module: `feature/<feature-name>`
2. Follow the feature structure (domain, data, presentation)
3. Create repository and ViewModel with UDF pattern
4. Add screens with Compose
5. Integrate navigation in the app
6. Add analytics events

See `docs/FEATURE_TEMPLATE.md` for detailed guide.

## 🧪 Testing

### Unit Tests
```kotlin
class LoginViewModelTest {
    @get:Rule
    val testDispatcher = TestDispatcherRule()

    @Test
    fun testLogin() = testDispatcher.runTest {
        // Test implementation
    }
}
```

### Integration Tests
```kotlin
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoginScreenDisplay() {
        // Compose UI test
    }
}
```

## 🔐 Security

- Token management via secure DataStore
- Certificate pinning placeholder in NetworkModule
- ProGuard rules configured for release builds
- Input validation in repositories

## 📊 Analytics

Track user actions and app events:

```kotlin
analyticsTracker.trackEvent(
    AnalyticsEvent.LoginSuccess(userId)
)
```

Current implementation uses debug logger. Replace with Firebase Analytics in production.

## 🎨 Theming

Material 3 theme with automatic light/dark mode support:

```kotlin
AndroidTemplateTheme {
    // Your content
}
```

Customize colors in `core:ui/theme/Color.kt`

## 📚 Additional Resources

- [Google Android Architecture](https://developer.android.com/jetpack/guide)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)

## 🤝 Contributing

1. Create a feature branch
2. Follow coding guidelines (see `docs/CODING_GUIDELINES.md`)
3. Ensure tests pass: `./gradlew test`
4. Run linting: `./gradlew detekt ktlint`
5. Submit pull request

## 📄 License

This project is provided as-is for educational and commercial use.

---

**Happy coding!** 🎉
