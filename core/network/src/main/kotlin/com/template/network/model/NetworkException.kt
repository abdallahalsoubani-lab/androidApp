package com.template.network.model

/**
 * Sealed class representing different network errors.
 */
sealed class NetworkException(
    message: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause) {
    /**
     * No internet connection available.
     */
    class NoInternetException : NetworkException("No internet connection")

    /**
     * Request timeout.
     */
    class TimeoutException : NetworkException("Request timeout")

    /**
     * HTTP error with status code.
     */
    data class HttpException(
        val code: Int,
        val errorBody: String? = null,
    ) : NetworkException("HTTP error: $code")

    /**
     * Failed to parse response.
     */
    class ParsingException(cause: Throwable? = null) :
        NetworkException("Failed to parse response", cause)

    /**
     * Other unknown network error.
     */
    data class UnknownException(val error: Throwable? = null) :
        NetworkException("Unknown network error", error)
}
