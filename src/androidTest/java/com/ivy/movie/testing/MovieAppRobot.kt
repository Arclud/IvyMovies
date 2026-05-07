package com.ivy.movie.testing

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.movie.MainActivity
import com.ivy.movie.presentation.TestTags

class MovieAppRobot(
    private val composeRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
) {

    fun loginAsAdmin() {
        composeRule.onNode(hasTestTag(TestTags.LoginInput).and(hasSetTextAction()))
            .performTextInput("admin")
        composeRule.onNode(hasTestTag(TestTags.PasswordInput).and(hasSetTextAction()))
            .performTextInput("admin")
        composeRule.onNode(hasTestTag(TestTags.SignInButton).and(hasClickAction()))
            .performClick()
        waitForHome()
    }

    fun waitForHome() {
        composeRule.waitUntil(timeoutMillis = DEFAULT_TIMEOUT) {
            composeRule.onAllNodes(hasTestTag(TestTags.HomeSearchInput))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    fun assertHomeSearchDisplayed() {
        composeRule.onNode(hasTestTag(TestTags.HomeSearchInput).and(hasSetTextAction()))
            .assertIsDisplayed()
    }

    fun waitUntilAnyMovieVisible() {
        composeRule.waitUntil(timeoutMillis = DEFAULT_TIMEOUT) {
            composeRule.onAllNodes(hasAnyMovieCard())
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    fun assertAnyMovieCardDisplayed() {
        composeRule.onAllNodes(hasAnyMovieCard().and(hasClickAction()))[0]
            .assertIsDisplayed()
    }

    fun typeSearch(query: String) {
        composeRule.onNode(hasTestTag(TestTags.HomeSearchInput).and(hasSetTextAction()))
            .performTextInput(query)
    }

    fun clearSearch() {
        composeRule.onNode(hasTestTag(TestTags.HomeSearchInput).and(hasSetTextAction()))
            .performTextClearance()
    }

    fun assertSearchQuery(query: String) {
        composeRule.onNode(hasTestTag(TestTags.HomeSearchInput).and(hasSetTextAction()))
            .assertTextContains(query)
    }

    fun waitUntilSearchSettles() {
        composeRule.waitUntil(timeoutMillis = SEARCH_TIMEOUT) {
            hasEmptyState() || hasMovieCards()
        }
    }

    fun waitUntilEmptyState() {
        composeRule.waitUntil(timeoutMillis = SEARCH_TIMEOUT) { hasEmptyState() }
    }

    fun assertEmptyStateDisplayed() {
        composeRule.onNode(hasText("Nothing found")).assertIsDisplayed()
    }

    private fun hasMovieCards(): Boolean {
        return composeRule.onAllNodes(hasAnyMovieCard())
            .fetchSemanticsNodes()
            .isNotEmpty()
    }

    private fun hasEmptyState(): Boolean {
        return composeRule.onAllNodes(hasText("Nothing found"))
            .fetchSemanticsNodes()
            .isNotEmpty()
    }

    private fun hasAnyMovieCard() = hasTestTagPrefix("movie_card_")

    private fun hasTestTagPrefix(prefix: String) = SemanticsMatcher(
        description = "has test tag prefix '$prefix'",
    ) { node ->
        val tag = if (node.config.contains(SemanticsProperties.TestTag)) {
            node.config[SemanticsProperties.TestTag]
        } else {
            null
        }
        tag?.startsWith(prefix) == true
    }

    companion object {
        private const val DEFAULT_TIMEOUT = 5_000L
        private const val SEARCH_TIMEOUT = 20_000L
    }
}
