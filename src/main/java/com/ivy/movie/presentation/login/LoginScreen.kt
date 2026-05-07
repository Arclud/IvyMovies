package com.ivy.movie.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.ivy.movie.presentation.TestTags
import com.ivy.movie.presentation.testTagAndContentDescription

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoggedIn: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                LoginEffect.NavigateHome -> onLoggedIn()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Ivy Movies", style = MaterialTheme.typography.headlineLarge)
        Text("Movies and series without the noise", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(28.dp))
        OutlinedTextField(
            value = state.login,
            onValueChange = { viewModel.onEvent(LoginEvent.LoginChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTagAndContentDescription(TestTags.LoginInput),
            singleLine = true,
            label = { Text("Login") },
            isError = state.error != null,
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTagAndContentDescription(TestTags.PasswordInput),
            singleLine = true,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = state.error != null,
        )
        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(20.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .testTagAndContentDescription(TestTags.SignInButton),
            onClick = { viewModel.onEvent(LoginEvent.Submit) },
        ) {
            Text("Sign in")
        }
    }
}
