# Feature Development Guide

This guide explains how to create a new feature following the project structure and patterns.

## Feature Structure

```
feature/myfeature/
├── src/main/kotlin/com/template/feature/myfeature/
│   ├── domain/
│   │   ├── model/
│   │   │   └── MyModel.kt
│   │   ├── repository/
│   │   │   └── MyRepository.kt
│   │   └── usecase/
│   │       └── MyUseCase.kt
│   ├── data/
│   │   ├── datasource/
│   │   │   ├── MyRemoteDataSource.kt
│   │   │   └── MyLocalDataSource.kt
│   │   ├── mapper/
│   │   │   └── MyModelMapper.kt
│   │   └── repository/
│   │       └── MyRepositoryImpl.kt
│   ├── presentation/
│   │   ├── viewmodel/
│   │   │   ├── MyViewModel.kt
│   │   │   └── MyUiState.kt (contains State, Event, Effect)
│   │   ├── screen/
│   │   │   └── MyScreen.kt
│   │   └── navigation/
│   │       └── MyNavigation.kt
│   └── di/
│       └── MyFeatureModule.kt
└── build.gradle.kts
```

## Step-by-Step Implementation

### 1. Create the Module

```bash
# In project root
mkdir -p feature/myfeature/src/main/{kotlin,res}
```

### 2. Create `build.gradle.kts`

```kotlin
plugins {
    id("template.android.library")
    id("template.android.compose")
    id("template.android.feature")
    id("template.android.hilt")
}

android {
    namespace = "com.template.feature.myfeature"
}

dependencies {
    // Core modules
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:analytics"))

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Testing
    testImplementation(project(":core:testing"))
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)

    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.junit)
}
```

### 3. Define Domain Models

File: `domain/model/MyModel.kt`

```kotlin
package com.template.feature.myfeature.domain.model

/**
 * Domain model - pure Kotlin, no Android dependencies
 */
data class MyModel(
    val id: String,
    val name: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis(),
)
```

### 4. Create Repository Interface

File: `domain/repository/MyRepository.kt`

```kotlin
package com.template.feature.myfeature.domain.repository

import com.template.common.Result
import com.template.feature.myfeature.domain.model.MyModel
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface - defined in domain layer
 */
interface MyRepository {
    /**
     * Fetch items with optional pagination
     */
    fun getItems(page: Int = 1): Flow<Result<List<MyModel>>>

    /**
     * Get single item by ID
     */
    suspend fun getItem(id: String): Result<MyModel>

    /**
     * Create new item
     */
    suspend fun createItem(name: String, description: String): Result<MyModel>

    /**
     * Update existing item
     */
    suspend fun updateItem(
        id: String,
        name: String,
        description: String,
    ): Result<MyModel>

    /**
     * Delete item
     */
    suspend fun deleteItem(id: String): Result<Unit>
}
```

### 5. Create Data Source Interfaces

File: `data/datasource/MyRemoteDataSource.kt`

```kotlin
package com.template.feature.myfeature.data.datasource

import com.template.feature.myfeature.domain.model.MyModel
import kotlinx.coroutines.flow.Flow

/**
 * Remote data source interface for network operations
 */
interface MyRemoteDataSource {
    suspend fun getItems(page: Int): List<MyModel>
    suspend fun getItem(id: String): MyModel
    suspend fun createItem(name: String, description: String): MyModel
    suspend fun updateItem(id: String, name: String, description: String): MyModel
    suspend fun deleteItem(id: String)
}
```

File: `data/datasource/MyLocalDataSource.kt`

```kotlin
package com.template.feature.myfeature.data.datasource

import com.template.feature.myfeature.domain.model.MyModel
import kotlinx.coroutines.flow.Flow

/**
 * Local data source interface for local storage
 */
interface MyLocalDataSource {
    fun getItems(): Flow<List<MyModel>>
    suspend fun getItem(id: String): MyModel?
    suspend fun saveItems(items: List<MyModel>)
    suspend fun saveItem(item: MyModel)
    suspend fun deleteItem(id: String)
    suspend fun clear()
}
```

### 6. Implement Repository

File: `data/repository/MyRepositoryImpl.kt`

```kotlin
package com.template.feature.myfeature.data.repository

import com.template.common.Result
import com.template.feature.myfeature.data.datasource.MyLocalDataSource
import com.template.feature.myfeature.data.datasource.MyRemoteDataSource
import com.template.feature.myfeature.domain.model.MyModel
import com.template.feature.myfeature.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Repository implementation with cache-first strategy:
 * 1. Fetch from remote
 * 2. Save to local cache
 * 3. Emit remote data
 * 4. On error, fallback to local cache
 */
class MyRepositoryImpl @Inject constructor(
    private val remoteDataSource: MyRemoteDataSource,
    private val localDataSource: MyLocalDataSource,
) : MyRepository {
    override fun getItems(page: Int): Flow<Result<List<MyModel>>> = flow {
        emit(Result.Loading)
        try {
            val remoteItems = remoteDataSource.getItems(page)
            localDataSource.saveItems(remoteItems)
            emit(Result.Success(remoteItems))
        } catch (e: Exception) {
            // Fallback to local cache
            localDataSource.getItems().collect { cachedItems ->
                if (cachedItems.isNotEmpty()) {
                    emit(Result.Success(cachedItems))
                } else {
                    emit(Result.Error(e))
                }
            }
        }
    }

    override suspend fun getItem(id: String): Result<MyModel> {
        return try {
            val item = remoteDataSource.getItem(id)
            localDataSource.saveItem(item)
            Result.Success(item)
        } catch (e: Exception) {
            val cached = localDataSource.getItem(id)
            if (cached != null) {
                Result.Success(cached)
            } else {
                Result.Error(e)
            }
        }
    }

    override suspend fun createItem(name: String, description: String): Result<MyModel> {
        return try {
            val item = remoteDataSource.createItem(name, description)
            localDataSource.saveItem(item)
            Result.Success(item)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateItem(
        id: String,
        name: String,
        description: String,
    ): Result<MyModel> {
        return try {
            val item = remoteDataSource.updateItem(id, name, description)
            localDataSource.saveItem(item)
            Result.Success(item)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteItem(id: String): Result<Unit> {
        return try {
            remoteDataSource.deleteItem(id)
            localDataSource.deleteItem(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
```

### 7. Create ViewModel with UDF

File: `presentation/viewmodel/MyViewModel.kt`

```kotlin
package com.template.feature.myfeature.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.template.analytics.AnalyticsEvent
import com.template.analytics.AnalyticsTracker
import com.template.common.Result
import com.template.common.dispatcher.DispatcherProvider
import com.template.feature.myfeature.domain.model.MyModel
import com.template.feature.myfeature.domain.repository.MyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// State
data class MyUiState(
    val isLoading: Boolean = false,
    val items: List<MyModel> = emptyList(),
    val error: String? = null,
)

// Events
sealed class MyUiEvent {
    object LoadItems : MyUiEvent()
    object RefreshItems : MyUiEvent()
    data class ItemClicked(val itemId: String) : MyUiEvent()
    object CreateClicked : MyUiEvent()
    object ClearError : MyUiEvent()
}

// Effects
sealed class MyUiEffect {
    data class NavigateToDetail(val itemId: String) : MyUiEffect()
    object NavigateToCreate : MyUiEffect()
    data class ShowToast(val message: String) : MyUiEffect()
}

@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<MyUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    fun onEvent(event: MyUiEvent) {
        when (event) {
            is MyUiEvent.LoadItems -> loadItems()
            is MyUiEvent.RefreshItems -> loadItems(refresh = true)
            is MyUiEvent.ItemClicked -> onItemClicked(event.itemId)
            is MyUiEvent.CreateClicked -> onCreateClicked()
            is MyUiEvent.ClearError -> clearError()
        }
    }

    private fun loadItems(refresh: Boolean = false) {
        viewModelScope.launch(dispatcherProvider.io()) {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getItems().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            items = result.data,
                            error = null,
                        )
                    }

                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.exception.message,
                        )
                    }

                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private fun onItemClicked(itemId: String) {
        viewModelScope.launch {
            _uiEffect.emit(MyUiEffect.NavigateToDetail(itemId))
        }
    }

    private fun onCreateClicked() {
        viewModelScope.launch {
            _uiEffect.emit(MyUiEffect.NavigateToCreate)
        }
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
```

### 8. Create Composable Screen

File: `presentation/screen/MyScreen.kt`

```kotlin
package com.template.feature.myfeature.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.template.feature.myfeature.presentation.viewmodel.MyUiEffect
import com.template.feature.myfeature.presentation.viewmodel.MyUiEvent
import com.template.feature.myfeature.presentation.viewmodel.MyViewModel
import com.template.ui.components.ErrorScreen
import com.template.ui.components.LoadingScreen

@Composable
fun MyScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: MyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(MyUiEvent.LoadItems)

        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is MyUiEffect.NavigateToDetail -> onNavigateToDetail(effect.itemId)
                is MyUiEffect.NavigateToCreate -> onNavigateToCreate()
                is MyUiEffect.ShowToast -> {
                    // Handle toast notification
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Feature") })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> LoadingScreen()
                uiState.error != null -> {
                    ErrorScreen(
                        message = uiState.error!!,
                        onRetry = { viewModel.onEvent(MyUiEvent.RefreshItems) }
                    )
                }

                else -> {
                    // Your content here
                    Text("Items: ${uiState.items.size}")
                }
            }
        }
    }
}
```

### 9. Setup Navigation

File: `navigation/MyNavigation.kt`

```kotlin
package com.template.feature.myfeature.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.template.feature.myfeature.presentation.screen.MyScreen

fun NavGraphBuilder.myFeatureGraph(navController: NavHostController) {
    navigation(
        route = "myfeature",
        startDestination = "myfeature_list"
    ) {
        composable("myfeature_list") {
            MyScreen(
                onNavigateToDetail = { itemId ->
                    navController.navigate("myfeature_detail/$itemId")
                },
                onNavigateToCreate = {
                    navController.navigate("myfeature_create")
                }
            )
        }

        composable("myfeature_detail/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            // Detail screen here
        }

        composable("myfeature_create") {
            // Create screen here
        }
    }
}
```

### 10. Create Hilt Module

File: `di/MyFeatureModule.kt`

```kotlin
package com.template.feature.myfeature.di

import com.template.feature.myfeature.data.repository.MyRepositoryImpl
import com.template.feature.myfeature.domain.repository.MyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MyFeatureModule {
    @Binds
    abstract fun bindMyRepository(
        impl: MyRepositoryImpl,
    ): MyRepository
}
```

### 11. Update app `TemplateApp.kt`

```kotlin
import com.template.feature.myfeature.navigation.myFeatureGraph

@Composable
fun TemplateApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "auth") {
        authGraph(navController)
        myFeatureGraph(navController)
    }
}
```

### 12. Update root `settings.gradle.kts`

```kotlin
include(":feature:myfeature")
```

## Testing Your Feature

### Unit Test Example

File: `src/test/kotlin/com/template/feature/myfeature/presentation/viewmodel/MyViewModelTest.kt`

```kotlin
class MyViewModelTest {
    @get:Rule
    val testDispatcher = TestDispatcherRule()

    private lateinit var viewModel: MyViewModel
    private val repository = MockkMapper<MyRepository>()
    private val analyticsTracker = mockk<AnalyticsTracker>()

    @Before
    fun setup() {
        viewModel = MyViewModel(repository, analyticsTracker, testDispatcher.testDispatcherProvider)
    }

    @Test
    fun loadItems_success() = testDispatcher.runTest {
        val mockItems = listOf(mockMyModel(), mockMyModel())
        coEvery { repository.getItems() } returns flowOf(Result.Success(mockItems))

        viewModel.onEvent(MyUiEvent.LoadItems)

        val state = viewModel.uiState.first { !it.isLoading }
        assertThat(state.items).isEqualTo(mockItems)
    }
}
```

## Best Practices Checklist

- [ ] No Android imports in domain layer
- [ ] Repository hides data source implementation
- [ ] ViewModel has no direct Android context
- [ ] UDF pattern properly implemented
- [ ] Proper error handling with Result type
- [ ] Analytics events tracked
- [ ] Unit tests for ViewModel and Repository
- [ ] UI tests for Composables
- [ ] Documentation in KDoc comments
- [ ] Proper Hilt dependency injection setup

---

Happy coding! Follow this template for consistent feature development across the project.
