package com.ivy.movie.domain

import com.ivy.movie.domain.auth.LoginResult
import com.ivy.movie.domain.auth.LoginUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setup() {
        loginUseCase = LoginUseCase()
    }

    @Test
    fun `empty login returns validation error`() = runTest {
        val result = loginUseCase(login = "", password = "admin")

        assertEquals(LoginResult.Error("Login is required"), result)
    }

    @Test
    fun `empty password returns validation error`() = runTest {
        val result = loginUseCase(login = "admin", password = "")

        assertEquals(LoginResult.Error("Password is required"), result)
    }

    @Test
    fun `admin credentials login successfully`() = runTest {
        val result = loginUseCase(login = "admin", password = "admin")

        assertEquals(LoginResult.Success, result)
    }

    @Test
    fun `wrong credentials return error`() = runTest {
        val result = loginUseCase(login = "admin", password = "wrong")

        assertEquals(LoginResult.Error("Incorrect login or password"), result)
    }
}
