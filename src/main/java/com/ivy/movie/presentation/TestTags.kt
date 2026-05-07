package com.ivy.movie.presentation

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

object TestTags {
    const val LoginInput = "login_input"
    const val PasswordInput = "password_input"
    const val SignInButton = "sign_in_button"
    const val HomeSearchInput = "home_search_input"
    const val ShimmerCard = "shimmer_card"

    fun movieCard(id: Long, type: String): String = "movie_card_${type}_$id"
}

fun Modifier.testTagAndContentDescription(tag: String): Modifier {
    return testTag(tag).semantics { contentDescription = tag }
}
