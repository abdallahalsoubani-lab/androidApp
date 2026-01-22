package com.template.common

/**
 * Base sealed class for all application exceptions.
 */
sealed class AppException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause) {
    /**
     * Exception thrown when network is unavailable.
     */
    class NetworkException(
        message: String? = null,
        cause: Throwable? = null
    ) : AppException(message ?: "Network error", cause)

    /**
     * Exception thrown when server returns an error.
     */
    data class ServerException(
        val code: Int,
        val errorMessage: String? = null
    ) : AppException("Server error: $code - $errorMessage")

    /**
     * Exception thrown when authentication fails.
     */
    class AuthException(
        message: String? = null,
        cause: Throwable? = null
    ) : AppException(message ?: "Authentication failed", cause)

    /**
     * Exception thrown when validation fails.
     */
    data class ValidationException(
        val errors: Map<String, String> = emptyMap()
    ) : AppException("Validation error: ${errors.entries.joinToString { "${it.key}: ${it.value}" }}")

    /**
     * Exception thrown for generic errors.
     */
    class GenericException(
        message: String? = null,
        cause: Throwable? = null
    ) : AppException(message ?: "An error occurred", cause)

    /**
     * Exception thrown when a resource is not found.
     */
    class NotFoundException(
        message: String? = null,
        cause: Throwable? = null
    ) : AppException(message ?: "Resource not found", cause)
}
