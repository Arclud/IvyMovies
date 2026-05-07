package com.ivy.movie.domain.auth

import javax.inject.Inject

sealed interface LoginResult {
    data object Success : LoginResult
    data class Error(val message: String) : LoginResult
}

class LoginUseCase @Inject constructor() {
    suspend operator fun invoke(login: String, password: String): LoginResult {
        val normalizedLogin = login.trim()
        val normalizedPassword = password.trim()
        return when {
            normalizedLogin.isBlank() -> LoginResult.Error("Login is required")
            normalizedPassword.isBlank() -> LoginResult.Error("Password is required")
            normalizedLogin == ADMIN && normalizedPassword == ADMIN -> LoginResult.Success
            else -> LoginResult.Error("Incorrect login or password")
        }
    }

    companion object {
        private const val ADMIN = "admin"
    }
}
