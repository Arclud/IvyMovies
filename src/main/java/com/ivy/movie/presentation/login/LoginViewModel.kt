package com.ivy.movie.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.movie.domain.auth.LoginResult
import com.ivy.movie.domain.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val login: String = "",
    val password: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
)

sealed interface LoginEvent {
    data class LoginChanged(val value: String) : LoginEvent
    data class PasswordChanged(val value: String) : LoginEvent
    data object Submit : LoginEvent
}

sealed interface LoginEffect {
    data object NavigateHome : LoginEffect
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {
    private val mutableState = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = mutableState.asStateFlow()

    private val mutableEffects = MutableSharedFlow<LoginEffect>()
    val effects: SharedFlow<LoginEffect> = mutableEffects.asSharedFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.LoginChanged -> mutableState.update { it.copy(login = event.value, error = null) }
            is LoginEvent.PasswordChanged -> mutableState.update { it.copy(password = event.value, error = null) }
            LoginEvent.Submit -> submit()
        }
    }

    private fun submit() {
        viewModelScope.launch {
            mutableState.update { it.copy(isLoading = true, error = null) }
            when (val result = loginUseCase(state.value.login, state.value.password)) {
                LoginResult.Success -> mutableEffects.emit(LoginEffect.NavigateHome)
                is LoginResult.Error -> mutableState.update { it.copy(error = result.message) }
            }
            mutableState.update { it.copy(isLoading = false) }
        }
    }
}
