package com.masmultimedia.sospechapp

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.masmultimedia.sospechapp.game.AppSettings
import com.masmultimedia.sospechapp.ui.components.LocalSospechSnackbarHostState
import com.masmultimedia.sospechapp.ui.settings.PRIVACY_OPTIONS_TAG
import com.masmultimedia.sospechapp.ui.settings.SettingsScreen
import com.masmultimedia.sospechapp.ui.theme.SospechAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun privacyOptionsAreHiddenWhenNotRequired() {
        setSettingsContent(showPrivacyOptions = false)

        composeRule.onNodeWithTag(PRIVACY_OPTIONS_TAG).assertDoesNotExist()
    }

    @Test
    fun privacyOptionsAreVisibleAndOpenOnlyAfterClickWhenRequired() {
        var clicks = 0
        setSettingsContent(
            showPrivacyOptions = true,
            onPrivacyOptionsClick = { clicks++ },
        )

        composeRule.runOnIdle { assertEquals(0, clicks) }
        composeRule.onNodeWithTag(PRIVACY_OPTIONS_TAG)
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()
        composeRule.runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun privacyOptionsErrorIsShownInSnackbar() {
        val snackbarHostState = SnackbarHostState()
        composeRule.setContent {
            PrivacyOptionsErrorEffect(
                eventId = 1,
                message = "localized error",
                snackbarHostState = snackbarHostState,
            )
        }

        composeRule.waitUntil { snackbarHostState.currentSnackbarData != null }
        composeRule.runOnIdle {
            assertEquals("localized error", snackbarHostState.currentSnackbarData?.visuals?.message)
        }
    }

    private fun setSettingsContent(
        showPrivacyOptions: Boolean,
        onPrivacyOptionsClick: () -> Unit = {},
    ) {
        composeRule.setContent {
            SospechAppTheme {
                CompositionLocalProvider(
                    LocalSospechSnackbarHostState provides SnackbarHostState(),
                ) {
                    SettingsScreen(
                        settings = AppSettings(),
                        showPrivacyOptions = showPrivacyOptions,
                        onBackClick = {},
                        onAction = {},
                        onPrivacyOptionsClick = onPrivacyOptionsClick,
                    )
                }
            }
        }
    }
}
