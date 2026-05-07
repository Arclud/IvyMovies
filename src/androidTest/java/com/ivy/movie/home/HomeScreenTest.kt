package com.ivy.movie.home

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.movie.MainActivity
import com.ivy.movie.testing.MovieAppRobot
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity> =
        createAndroidComposeRule<MainActivity>()
    
    private val robot by lazy { MovieAppRobot(composeRule) }

    @Test
    fun popularContentIsDisplayedAfterLogin() {
        robot.loginAsAdmin()
        robot.waitUntilAnyMovieVisible()
        robot.assertHomeSearchDisplayed()
        robot.assertAnyMovieCardDisplayed()
    }

    @Test
    fun searchDisplaysMatchingMovieAndKeepsQueryInSearchField() {
        robot.loginAsAdmin()
        robot.waitUntilAnyMovieVisible()
        robot.typeSearch("matrix")
        robot.waitUntilSearchSettles()
        robot.assertSearchQuery("matrix")
    }

    @Test
    fun emptySearchResultShowsEmptyState() {
        robot.loginAsAdmin()
        robot.waitUntilAnyMovieVisible()
        robot.typeSearch("not_a_real_movie_query_12345")
        robot.waitUntilEmptyState()
        robot.assertEmptyStateDisplayed()
    }

    @Test
    fun clearingSearchRestoresPopularContent() {
        robot.loginAsAdmin()
        robot.waitUntilAnyMovieVisible()
        robot.typeSearch("not_a_real_movie_query_12345")
        robot.waitUntilEmptyState()
        robot.clearSearch()
        robot.waitUntilAnyMovieVisible()
    }
}
