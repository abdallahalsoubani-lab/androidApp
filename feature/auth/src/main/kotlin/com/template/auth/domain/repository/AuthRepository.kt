package com.template.auth.domain.repository

import com.template.auth.domain.model.User
import com.template.common.Result

/**
 * Repository interface for authentication operations.
 */
interface AuthRepository {
    /**
     * Attempt to login with email and password.
     */
    suspend fun login(email: String, password: String): Result<User>

    /**
     * Logout the current user.
     */
    suspend fun logout(): Result<Unit>

    /**
     * Get the current user.
     */
    suspend fun getCurrentUser(): Result<User?>

    /**
     * Check if user is logged in.
     */
    suspend fun isLoggedIn(): Result<Boolean>

    /**
     * Register a new user.
     */
    suspend fun register(
        email: String,
        password: String,
        name: String,
    ): Result<User>

    /**
     * Verify OTP.
     */
    suspend fun verifyOtp(email: String, otp: String): Result<Unit>
}
