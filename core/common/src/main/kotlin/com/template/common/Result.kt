package com.template.common

/**
 * Sealed class representing the result of an asynchronous operation.
 * Can be [Success], [Error], or [Loading].
 */
sealed class Result<out T> {
    /**
     * Represents a successful operation with data.
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Represents a failed operation with an exception.
     */
    data class Error(val exception: Throwable) : Result<Nothing>()

    /**
     * Represents an ongoing operation.
     */
    object Loading : Result<Nothing>()
}

val <T> Result<T>.successOr: T?
    get() = (this as? Result.Success<T>)?.data

/**
 * Returns the encapsulated value if this instance represents [Result.Success].
 * Otherwise, it throws the exception from [Result.Error] or returns null for [Result.Loading].
 */
inline fun <T> Result<T>.getOrNull(): T? = when (this) {
    is Result.Success -> data
    is Result.Error -> null
    is Result.Loading -> null
}

/**
 * Returns the encapsulated exception if this instance represents [Result.Error].
 * Otherwise, it returns null.
 */
inline fun <T> Result<T>.exceptionOrNull(): Throwable? = when (this) {
    is Result.Error -> exception
    else -> null
}

/**
 * Maps the success value using the given transform function.
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> Result.Error(exception)
    is Result.Loading -> Result.Loading
}

/**
 * Maps the error using the given transform function.
 */
inline fun <T> Result<T>.mapError(transform: (Throwable) -> Throwable): Result<T> = when (this) {
    is Result.Error -> Result.Error(transform(exception))
    else -> this
}
