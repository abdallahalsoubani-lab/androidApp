# Architecture Guide

## Overview

This project follows **Clean Architecture** with **MVVM** pattern and **Unidirectional Data Flow**.

```
Presentation Layer (UI)
         ↓
ViewModel (State + Logic)
         ↓
UseCase/Repository (Domain)
         ↓
DataSource (Network/Database)
```

## Core Principles

### 1. Clean Architecture Layers

#### Presentation Layer
- **Responsibility**: UI rendering and user interaction
- **Location**: `feature/*/presentation/`
- **Components**: Composables, ViewModels
- **Rules**:
  - No business logic
  - No direct data source access
  - Should be easily testable

#### Domain Layer
- **Responsibility**: Business logic and rules
- **Location**: `feature/*/domain/`
- **Components**: Models, Repository interfaces, UseCases
- **Rules**:
  - No Android imports
  - Pure Kotlin
  - Highest level of abstraction
  - Most reusable and testable

#### Data Layer
- **Responsibility**: Data access and persistence
- **Location**: `feature/*/data/`
- **Components**: Repository implementations, DataSources, DTOs, Mappers
- **Rules**:
  - Implements domain repository interfaces
  - Handles data transformations (DTO → Entity → Model)
  - Manages caching strategies

### 2. Unidirectional Data Flow (UDF)

Every screen implements three components:

```kotlin
// 1. State: Single source of truth
data class MyScreenUiState(
    val isLoading: Boolean = false,
    val data: List<Item> = emptyList(),
    val selectedId: String? = null,
    val error: String? = null,
)

// 2. Events: User intents
sealed class MyScreenUiEvent {
    object LoadData : MyScreenUiEvent()
    data class ItemClicked(val id: String) : MyScreenUiEvent()
    data class SearchChanged(val query: String) : MyScreenUiEvent()
    object RefreshRequested : MyScreenUiEvent()
}

// 3. Effects: One-shot side effects
sealed class MyScreenUiEffect {
    data class ShowToast(val message: String) : MyScreenUiEffect()
    data class NavigateTo(val screen: String) : MyScreenUiEffect()
    object ShowDeleteConfirm : MyScreenUiEffect()
}
```

**Flow**:
1. User triggers action → Event
2. ViewModel processes event → Updates State
3. UI observes state → Renders
4. For navigation/toast → Effect

### 3. Repository Pattern

Single point of data access:

```kotlin
interface UserRepository {
    fun getUsers(): Flow<Result<List<User>>>
    suspend fun getUser(id: String): Result<User>
    suspend fun saveUser(user: User): Result<Unit>
    suspend fun deleteUser(id: String): Result<Unit>
}

class UserRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource,
) : UserRepository {
    override fun getUsers(): Flow<Result<List<User>>> = flow {
        emit(Result.Loading)
        try {
            // Try remote first
            val users = remoteDataSource.getUsers()
            // Save to local cache
            localDataSource.saveUsers(users)
            emit(Result.Success(users))
        } catch (e: Exception) {
            // Fallback to local cache
            val cached = localDataSource.getUsers()
            if (cached.isNotEmpty()) {
                emit(Result.Success(cached))
            } else {
                emit(Result.Error(e))
            }
        }
    }
}
```

### 4. Dependency Injection with Hilt

All dependencies are injected via Hilt:

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {
    // Use injected dependencies
}
```

## Module Organization

### Core Modules

#### `:core:ui`
- Design system and Material 3 theme
- Reusable Composables
- Common screen patterns (Loading, Error, Empty)

#### `:core:network`
- Retrofit setup
- OkHttp interceptors
- NetworkMonitor for connectivity
- Unified error handling

#### `:core:database`
- Room Database setup
- Base DAO pattern
- Migration strategy

#### `:core:datastore`
- User preferences using DataStore
- Secure preferences placeholder
- Global app settings

#### `:core:common`
- `Result<T>` sealed class for operation results
- `AppException` sealed class
- DispatcherProvider for testable coroutines
- Extension functions and utilities

#### `:core:analytics`
- AnalyticsTracker interface
- Event definitions
- Multiple implementations (Debug, Firebase)

#### `:core:testing`
- Test utilities
- Fake repositories and data sources
- Mock data builders
- Test dispatcher rule

### Feature Modules

Each feature follows the same pattern:

```
feature/name/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── data/
│   ├── datasource/
│   ├── mapper/
│   └── repository/
├── presentation/
│   ├── viewmodel/
│   ├── screen/
│   └── navigation/
└── build.gradle.kts
```

## Data Flow Example

### Feature: User List

1. **Presentation Layer**
   ```kotlin
   @Composable
   fun UserListScreen(onUserClicked: (String) -> Unit) {
       val viewModel: UserListViewModel = hiltViewModel()
       val state by viewModel.uiState.collectAsStateWithLifecycle()

       LaunchedEffect(Unit) {
           viewModel.onEvent(UserListUiEvent.LoadUsers)
       }

       UserListContent(
           state = state,
           onUserClicked = { userId ->
               viewModel.onEvent(UserListUiEvent.UserClicked(userId))
           }
       )
   }
   ```

2. **ViewModel Layer**
   ```kotlin
   fun onEvent(event: UserListUiEvent) {
       when (event) {
           is UserListUiEvent.LoadUsers -> loadUsers()
           is UserListUiEvent.UserClicked -> navigateToUser(event.id)
       }
   }

   private fun loadUsers() {
       viewModelScope.launch {
           _uiState.value = _uiState.value.copy(isLoading = true)
           when (val result = userRepository.getUsers()) {
               is Result.Success -> {
                   _uiState.value = _uiState.value.copy(users = result.data)
               }
               is Result.Error -> {
                   _uiState.value = _uiState.value.copy(error = result.exception.message)
               }
           }
       }
   }
   ```

3. **Domain Layer**
   ```kotlin
   interface UserRepository {
       suspend fun getUsers(): Result<List<User>>
   }
   ```

4. **Data Layer**
   ```kotlin
   class UserRepositoryImpl(
       private val api: UserApi,
       private val dao: UserDao,
   ) : UserRepository {
       override suspend fun getUsers(): Result<List<User>> {
           return try {
               val remoteUsers = api.getUsers()
               dao.insertUsers(remoteUsers.map { it.toEntity() })
               Result.Success(remoteUsers)
           } catch (e: Exception) {
               Result.Error(e)
           }
       }
   }
   ```

## Best Practices

### 1. Immutable State
```kotlin
// ❌ Bad: Mutable state
var isLoading = false

// ✅ Good: Immutable state
data class UiState(val isLoading: Boolean = false)
```

### 2. Single Responsibility
```kotlin
// ❌ Bad: Mixing concerns
class UserViewModel(private val context: Context) {
    fun saveUser() {
        // Handles API call, database, UI logic
    }
}

// ✅ Good: Separated concerns
class UserViewModel(private val repository: UserRepository) {
    fun saveUser() {
        // Delegates to repository
    }
}
```

### 3. Error Handling
```kotlin
// ❌ Bad: Throwing exceptions
suspend fun getUser(): User {
    return api.getUser()  // Throws on failure
}

// ✅ Good: Using Result type
suspend fun getUser(): Result<User> {
    return try {
        Result.Success(api.getUser())
    } catch (e: Exception) {
        Result.Error(e)
    }
}
```

### 4. Testing
```kotlin
// ✅ Good: Testable with dependency injection
class UserViewModelTest {
    private val fakeRepository = FakeUserRepository()
    private val viewModel = UserViewModel(fakeRepository)

    @Test
    fun testLoadUsers() {
        fakeRepository.setData(listOf(mockUser))
        viewModel.onEvent(UserListUiEvent.LoadUsers)
        // Assert state
    }
}
```

## Navigation

Using Jetpack Navigation Compose with type-safe routes:

```kotlin
NavHost(navController, startDestination = "auth") {
    authGraph(navController)
    homeGraph(navController)
}
```

## State Management Rules

1. **Single Source of Truth**: Use StateFlow for UI state
2. **Immutability**: Never mutate state objects
3. **Unidirectional**: State flows down, events flow up
4. **Testability**: Mock all dependencies
5. **Performance**: Use remember, derivedStateOf for optimization

---

For more details, see individual feature documentation and code examples.
