package com.template.auth.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is LoginUiEffect.NavigateToHome -> onNavigateToHome()
                is LoginUiEffect.ShowToast -> {
                    // Handle toast
                }

                is LoginUiEffect.NavigateToOtp -> {
                    // Handle OTP navigation
                }
            }
        }
    }

    LoginScreenContent(
        uiState = uiState,
        onEmailChange = { viewModel.onEvent(LoginUiEvent.EmailChanged(it)) },
        onPasswordChange = { viewModel.onEvent(LoginUiEvent.PasswordChanged(it)) },
        onLoginClick = { viewModel.onEvent(LoginUiEvent.LoginClicked) },
        onSignUpClick = onNavigateToSignUp,
    )
}

@Composable
private fun LoginScreenContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Welcome",
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
        )

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !uiState.isLoading,
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Don't have an account? Sign up",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}
