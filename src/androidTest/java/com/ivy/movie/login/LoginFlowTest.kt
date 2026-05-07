package com.ivy.movie.login

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.ivy.movie.MainActivity
import com.ivy.movie.testing.MovieAppRobot
import org.junit.Rule
import org.junit.Test

class LoginFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()
    private val robot by lazy { MovieAppRobot(composeRule) }

    @Test
    fun adminCredentialsOpenHomeScreen() {
        robot.loginAsAdmin()
        robot.assertHomeSearchDisplayed()
    }
}
