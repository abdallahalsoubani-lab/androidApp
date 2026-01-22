package com.template.testing.fakes

import com.template.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Base class for fake repositories used in testing.
 * Provides common patterns for mocking repository behavior.
 */
abstract class FakeRepository<T> {
    protected val _data = MutableStateFlow<Result<List<T>>>(Result.Loading)
    val data: Flow<Result<List<T>>> = _data.asStateFlow()

    protected val _error = MutableStateFlow<String?>(null)
    val error: Flow<String?> = _error.asStateFlow()

    fun setData(items: List<T>) {
        _data.value = Result.Success(items)
    }

    fun setLoading() {
        _data.value = Result.Loading
    }

    fun setError(message: String) {
        _error.value = message
        _data.value = Result.Error(Exception(message))
    }

    fun clear() {
        _data.value = Result.Success(emptyList())
        _error.value = null
    }
}
