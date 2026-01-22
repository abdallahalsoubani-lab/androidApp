package com.template.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.template.analytics.AnalyticsEvent
import com.template.analytics.AnalyticsTracker
import com.template.auth.domain.model.User
import com.template.auth.domain.repository.AuthRepository
import com.template.common.Result
import com.template.common.dispatcher.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UDF (Unidirectional Data Flow) pattern implementation.
 */

/**
 * Immutable UI state for login screen.
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = "",
    val user: User? = null,
    val error: String? = null,
)

/**
 * User intents/events for the login screen.
 */
sealed class LoginUiEvent {
    data class EmailChanged(val email: String) : LoginUiEvent()
    data class PasswordChanged(val password: String) : LoginUiEvent()
    object LoginClicked : LoginUiEvent()
    object ClearError : LoginUiEvent()
}

/**
 * One-shot effects for navigation or toast messages.
 */
sealed class LoginUiEffect {
    data class ShowToast(val message: String) : LoginUiEffect()
    object NavigateToHome : LoginUiEffect()
    object NavigateToOtp : LoginUiEffect()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<LoginUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.EmailChanged -> {
                _uiState.value = _uiState.value.copy(email = event.email)
            }

            is LoginUiEvent.PasswordChanged -> {
                _uiState.value = _uiState.value.copy(password = event.password)
            }

            is LoginUiEvent.LoginClicked -> {
                login()
            }

            is LoginUiEvent.ClearError -> {
                _uiState.value = _uiState.value.copy(error = null)
            }
        }
    }

    private fun login() {
        val currentState = _uiState.value
        if (currentState.email.isEmpty() || currentState.password.isEmpty()) {
            _uiState.value = currentState.copy(error = "Please fill in all fields")
            return
        }

        viewModelScope.launch(dispatcherProvider.io()) {
            _uiState.value = currentState.copy(isLoading = true)

            analyticsTracker.trackEvent(
                AnalyticsEvent.LoginAttempt(currentState.email)
            )

            when (val result = authRepository.login(currentState.email, currentState.password)) {
                is Result.Success -> {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        user = result.data,
                        error = null,
                    )
                    analyticsTracker.trackEvent(
                        AnalyticsEvent.LoginSuccess(result.data.id)
                    )
                    _uiEffect.emit(LoginUiEffect.NavigateToHome)
                }

                is Result.Error -> {
                    val errorMessage = result.exception.message ?: "Login failed"
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        error = errorMessage,
                    )
                    analyticsTracker.trackEvent(
                        AnalyticsEvent.LoginFailure(errorMessage)
                    )
                    _uiEffect.emit(LoginUiEffect.ShowToast(errorMessage))
                }

                is Result.Loading -> {
                    // Already set loading state
                }
            }
        }
    }
}
