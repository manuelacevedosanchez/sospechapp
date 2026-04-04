package com.masmultimedia.sospechapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainMenuScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun mainMenu_showsNewGame_andCanClick() {
        // Checks that the "New Game" button is visible and can be clicked
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val newGameText = context.getString(R.string.main_new_game)
        composeTestRule.onNodeWithText(newGameText).assertExists().performClick()
    }

    @Test
    fun mainMenu_showsHowToPlay() {
        // Checks that the "How to play?" button is visible
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val howToPlayText = context.getString(R.string.main_how_to_play)
        composeTestRule.onNodeWithText(howToPlayText).assertExists()
    }
}
