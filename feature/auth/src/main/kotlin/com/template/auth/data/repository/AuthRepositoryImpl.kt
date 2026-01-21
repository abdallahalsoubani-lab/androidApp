package com.template.auth.data.repository

import com.template.auth.domain.model.User
import com.template.auth.domain.repository.AuthRepository
import com.template.common.AppException
import com.template.common.Result
import com.template.datastore.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * Implementation of [AuthRepository].
 */
class AuthRepositoryImpl @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            // TODO: Call actual login API
            // For now, return a mock user
            val user = User(
                id = "user_123",
                email = email,
                name = email.substringBefore("@"),
            )

            userPreferencesRepository.setLoggedIn(true)
            userPreferencesRepository.setUserToken("mock_token_${System.currentTimeMillis()}")
            userPreferencesRepository.setUserId(user.id)

            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(
                AppException.AuthException(
                    message = e.message ?: "Login failed"
                )
            )
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            userPreferencesRepository.setLoggedIn(false)
            userPreferencesRepository.clearAll()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            // TODO: Fetch from API or cache
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun isLoggedIn(): Result<Boolean> {
        return try {
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String,
    ): Result<User> {
        return try {
            // TODO: Call actual register API
            val user = User(
                id = "user_${System.currentTimeMillis()}",
                email = email,
                name = name,
            )
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun verifyOtp(email: String, otp: String): Result<Unit> {
        return try {
            // TODO: Call actual OTP verification API
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
