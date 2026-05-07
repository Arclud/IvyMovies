package com.ivy.movie.login

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.ivy.movie.presentation.TestTags
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginFlowTest {

    private lateinit var device: UiDevice

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()
    }

    @Test
    fun adminCredentialsOpenHomeScreen() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(PACKAGE_NAME)!!.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        context.startActivity(intent)

        assertTrue(device.wait(Until.hasObject(By.text("Ivy Movies")), TIMEOUT))
        device.enterText(TestTags.LoginInput, "admin")
        device.enterText(TestTags.PasswordInput, "admin")

        device.waitForObject(TestTags.SignInButton).click()

        assertTrue(device.wait(Until.hasObject(By.text("Home")), TIMEOUT))
        assertTrue(device.wait(Until.hasObject(By.desc(TestTags.HomeSearchInput)), TIMEOUT))
    }

    private fun UiDevice.waitForObject(contentDescription: String) =
        wait(Until.findObject(By.desc(contentDescription)), TIMEOUT)
            ?: error("Object with contentDescription=$contentDescription was not found")

    private fun UiDevice.enterText(contentDescription: String, text: String) {
        waitForObject(contentDescription).click()
        executeShellCommand("input text $text")
    }

    companion object {
        private const val PACKAGE_NAME = "com.ivy.movie"
        private const val TIMEOUT = 5_000L
    }
}
