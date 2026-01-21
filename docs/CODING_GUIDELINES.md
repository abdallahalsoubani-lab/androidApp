# Coding Guidelines

## Overview

This document outlines the coding standards and conventions used in this project.

## Kotlin Style Guide

We follow [Kotlin Official Style Guide](https://kotlinlang.org/docs/coding-conventions.html) with the following modifications:

### File Organization

```kotlin
// 1. License/Copyright header
// 2. Package declaration
package com.template.feature

// 3. Imports (organized, no wildcards)
import android.content.Context
import androidx.compose.runtime.Composable
import com.template.common.Result
import kotlinx.coroutines.flow.Flow

// 4. Top-level declarations
// - Constants
// - Type aliases
// - Functions
// - Classes
// - Objects

const val CONSTANT_NAME = 100

// 5. Class declaration with proper ordering:
// - Properties
// - Companion object
// - Constructors
// - Method declarations (group by responsibility)
// - Override methods
// - Inner classes
class MyClass(val context: Context) {
    companion object {
        private const val TAG = "MyClass"
    }

    private val mutableState = mutableListOf<String>()

    fun myMethod() {}

    override fun toString(): String = super.toString()
}
```

### Naming Conventions

```kotlin
// Classes, Interfaces, Enums - PascalCase
class MyClass
interface MyInterface
enum class MyEnum

// Functions, variables, parameters - camelCase
fun myFunction() {}
val myVariable = 0
var myProperty = ""

// Constants - UPPER_SNAKE_CASE
const val MAX_SIZE = 100
const val DEFAULT_TIMEOUT = 30000L

// Private properties - _camelCase prefix optional
private val _privateState = MutableStateFlow(0)
private var _internalValue = ""

// Backing properties
private val _state = MutableStateFlow<UiState>()
val state: StateFlow<UiState> = _state.asStateFlow()

// Boolean properties - is/has prefix
val isEmpty: Boolean
val hasError: Boolean
var isLoading: Boolean
```

### Code Style

#### Formatting

```kotlin
// Line length: Max 120 characters

// Use 4 spaces for indentation
fun myFunction() {
    if (condition) {
        doSomething()
    }
}

// Braces: Always use braces, even for single-line statements
if (condition) {
    action()
}

// Not:
if (condition) action()
```

#### Functions

```kotlin
// Single expression functions
fun getValue() = computeValue()

// Multi-line functions
fun complexFunction(
    param1: String,
    param2: Int,
    param3: Boolean,
): String {
    return when {
        condition1 -> result1
        condition2 -> result2
        else -> result3
    }
}

// Extension functions
fun <T> Flow<T>.collectWith(action: suspend (T) -> Unit) = collect(action)
```

#### Collections and Lambdas

```kotlin
// Use trailing lambdas
list.map { item -> item.value }
    .filter { value -> value > 0 }

// Single parameter lambdas use 'it'
list.map { it.toString() }

// Named parameters for clarity
myFunction(
    items = listOf(1, 2, 3),
    transform = { it * 2 },
    predicate = { it > 0 }
)
```

#### Null Safety

```kotlin
// Use let for null-safe operations
myNullableValue?.let { value ->
    doSomething(value)
}

// Use require/check for preconditions
fun myFunction(param: String) {
    require(param.isNotEmpty()) { "Param cannot be empty" }
}

// Avoid !!
// ❌ myNullableValue!!.doSomething()

// ✅ myNullableValue?.doSomething() ?: run { handleNull() }
```

#### When Expressions

```kotlin
// Use when for multiple conditions
fun getAction(event: MyEvent) = when (event) {
    is MyEvent.Click -> handleClick()
    is MyEvent.Submit -> handleSubmit()
    is MyEvent.Cancel -> handleCancel()
}

// Always include else for sealed classes if not exhaustive
when (value) {
    is TypeA -> {}
    is TypeB -> {}
    // else is not needed for exhaustive sealed classes
}
```

### Architecture Guidelines

#### Clean Architecture Layers

```kotlin
// ❌ Bad: Mixing layers
class UserViewModel(private val context: Context) {
    fun saveUser() {
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "db").build()
        db.userDao().insert(user)
    }
}

// ✅ Good: Separated layers
class UserViewModel(private val repository: UserRepository) : ViewModel() {
    fun saveUser() {
        viewModelScope.launch {
            repository.saveUser(user)
        }
    }
}

// ✅ Domain layer - pure Kotlin
interface UserRepository {
    suspend fun saveUser(user: User): Result<Unit>
}

// ✅ Data layer - implementation
class UserRepositoryImpl(private val dao: UserDao) : UserRepository {
    override suspend fun saveUser(user: User): Result<Unit> = try {
        dao.insert(user.toEntity())
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
```

#### Repository Pattern

```kotlin
// ✅ Good: Single responsibility, easy to test
interface ItemRepository {
    fun getItems(): Flow<Result<List<Item>>>
    suspend fun getItem(id: String): Result<Item>
    suspend fun createItem(item: Item): Result<Item>
    suspend fun updateItem(item: Item): Result<Item>
    suspend fun deleteItem(id: String): Result<Unit>
}

// ✅ Good: Cache strategy
class ItemRepositoryImpl(
    private val remoteDataSource: ItemRemoteDataSource,
    private val localDataSource: ItemLocalDataSource,
) : ItemRepository {
    override fun getItems(): Flow<Result<List<Item>>> = flow {
        emit(Result.Loading)
        try {
            val remoteItems = remoteDataSource.getItems()
            localDataSource.saveItems(remoteItems)
            emit(Result.Success(remoteItems))
        } catch (e: Exception) {
            localDataSource.getItems().collect { cached ->
                if (cached.isNotEmpty()) {
                    emit(Result.Success(cached))
                } else {
                    emit(Result.Error(e))
                }
            }
        }
    }
}
```

#### State Management (UDF)

```kotlin
// ✅ Good: Immutable state
data class MyUiState(
    val isLoading: Boolean = false,
    val data: List<Item> = emptyList(),
    val error: String? = null,
)

sealed class MyUiEvent {
    object LoadData : MyUiEvent()
    data class ItemClicked(val id: String) : MyUiEvent()
}

sealed class MyUiEffect {
    data class ShowToast(val message: String) : MyUiEffect()
    data class NavigateTo(val route: String) : MyUiEffect()
}

// ✅ Good: ViewModel
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<MyUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    fun onEvent(event: MyUiEvent) {
        when (event) {
            is MyUiEvent.LoadData -> loadData()
            is MyUiEvent.ItemClicked -> onItemClicked(event.id)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = repository.getData()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        data = result.data,
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message,
                    )
                }
                is Result.Loading -> {}
            }
        }
    }
}
```

### Comments and Documentation

```kotlin
// Use KDoc for public APIs
/**
 * Fetches a list of items with pagination.
 *
 * @param page The page number (1-indexed)
 * @param pageSize The number of items per page
 * @return A Flow emitting [Result] with list of items
 * @throws IllegalArgumentException if page or pageSize is invalid
 *
 * @see Result
 */
fun getItems(page: Int = 1, pageSize: Int = 20): Flow<Result<List<Item>>>

// Use inline comments sparingly, only for non-obvious logic
val adjustedValue = value * SCALE_FACTOR // Account for DPI difference

// Use // TODO: FIXME: HACK: comments for temporary code
// TODO: Replace with actual API call once endpoint is available
val mockData = listOf(mockItem)

// Document complex algorithms
/**
 * Implements merge sort algorithm.
 * Time complexity: O(n log n)
 * Space complexity: O(n)
 */
fun mergeSort(list: List<Int>): List<Int> { }
```

### Testing Guidelines

```kotlin
// ✅ Good: Clear test naming
@Test
fun testLoginWithValidCredentials_succeeds() {
    // Given
    val email = "test@example.com"
    val password = "password123"

    // When
    viewModel.onEvent(LoginEvent.Login(email, password))

    // Then
    assertEquals(LoginState.Success, viewModel.uiState.value)
}

// ✅ Good: Use descriptive assertions
assertTrue(viewModel.uiState.value.isLoading)
assertThat(viewModel.uiState.value.items).isEqualTo(expectedItems)

// ✅ Good: Arrange-Act-Assert pattern
@Test
fun testCalculateDiscount() {
    // Arrange
    val price = 100.0
    val discountPercent = 10

    // Act
    val result = calculateDiscount(price, discountPercent)

    // Assert
    assertEquals(90.0, result)
}

// ✅ Good: Test dependencies with mocks
@Test
fun testRepository_savesItemToLocalSource() {
    // Arrange
    val item = mockItem()
    coEvery { localDataSource.saveItem(item) } returns Unit

    // Act
    runTest {
        repository.saveItem(item)
    }

    // Assert
    coVerify { localDataSource.saveItem(item) }
}
```

### Common Mistakes to Avoid

```kotlin
// ❌ Don't: Mutable collections in public APIs
fun getItems(): MutableList<Item> = mutableListOf()

// ✅ Do: Immutable collections
fun getItems(): List<Item> = listOf()

// ❌ Don't: String concatenation
val message = "Hello " + name + "!"

// ✅ Do: String templates
val message = "Hello $name!"

// ❌ Don't: Unchecked casts
@Suppress("UNCHECKED_CAST")
val items = obj as List<Item>

// ✅ Do: Safe casts with type checking
val items = (obj as? List<Item>) ?: emptyList()

// ❌ Don't: Complex nested lambdas
list.map { item ->
    item.values.map { value ->
        value * 2
    }.filter { it > 0 }
}

// ✅ Do: Extract to intermediate variables
list
    .map { item -> item.values }
    .flatten()
    .map { it * 2 }
    .filter { it > 0 }

// ❌ Don't: Global state
object UserManager {
    var currentUser: User? = null
}

// ✅ Do: Proper dependency injection
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
)
```

### Performance Guidelines

```kotlin
// ❌ Don't: Creating objects in loops
for (i in 0..1000) {
    val config = Configuration() // Bad: Creates 1000 objects
}

// ✅ Do: Reuse objects or use immutable data structures
val config = Configuration()
repeat(1000) { index ->
    process(config, index)
}

// ❌ Don't: Inefficient collection operations
val result = list.filter { it > 0 }.map { it * 2 }.filter { it < 100 }

// ✅ Do: Chain efficiently
val result = list
    .filter { it in 1..49 }
    .map { it * 2 }

// ✅ Do: Use appropriate collection operations
val result = mutableListOf<Int>()
for (item in list) {
    if (item > 0) {
        result.add(item * 2)
    }
}
```

## IDE Configuration

### Android Studio
- Use EditorConfig plugin for consistent formatting
- Run `./gradlew ktlintFormat` before committing
- Enable code inspections and warnings

### Pre-commit Hook

Create `.git/hooks/pre-commit`:
```bash
#!/bin/bash
./gradlew ktlint
if [ $? -ne 0 ]; then
    echo "ktlint failed. Run ./gradlew ktlintFormat to fix."
    exit 1
fi
```

## Review Checklist

Before submitting a pull request, ensure:

- [ ] Code follows naming conventions
- [ ] Functions have single responsibility
- [ ] No hardcoded values (use constants)
- [ ] Proper error handling with Result type
- [ ] No TODO/FIXME left in production code
- [ ] Unit tests for business logic
- [ ] No unnecessary comments
- [ ] Clean git history with descriptive commits
- [ ] All tests passing locally
- [ ] No code smells or warnings from detekt/ktlint

---

For more details, refer to:
- [Kotlin Official Documentation](https://kotlinlang.org/docs/)
- [Android Architecture Guide](https://developer.android.com/jetpack/guide)
- [Google's Android Code Style Guide](https://source.android.com/setup/contribute/code-style)
