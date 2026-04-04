package com.masmultimedia.sospechapp

import android.util.Log
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@RunWith(AndroidJUnit4::class)
class MainMenuScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun takeScreenshot(device: UiDevice, tag: String) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "/sdcard/sospechapp_test_${tag}_$timestamp.png"
        val success = device.takeScreenshot(File(fileName))
        Log.d("MainMenuScreenTest", "Screenshot taken: $fileName, success: $success")
    }

    @Test
    fun mainMenu_showsNewGame_andCanClick() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        // Checks that the "New Game" button is visible and can be clicked (robust to language)
        val variants = listOf("Nueva partida", "New Game")
        var found = false
        for (variant in variants) {
            try {
                composeTestRule.onNodeWithText(variant, substring = true).assertExists()
                    .performClick()
                Log.d("MainMenuScreenTest", "Found and clicked New Game variant: $variant")
                found = true
                break
            } catch (_: AssertionError) {
                Log.w("MainMenuScreenTest", "New Game variant not found: $variant")
            }
        }
        if (!found) {
            Log.e("MainMenuScreenTest", "No New Game button found in any variant")
            takeScreenshot(device, "no_new_game")
        }
        assert(found) { "No se encontró el botón de nueva partida en español o inglés" }
    }

    @Test
    fun mainMenu_showsHowToPlay() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        // Checks that the "How to play?" button is visible (robust to language)
        val variants = listOf("Cómo se juega", "Como se juega", "How to play")
        var found = false
        for (variant in variants) {
            try {
                composeTestRule.onNodeWithText(variant, substring = true).assertExists()
                Log.d("MainMenuScreenTest", "Found How to play variant: $variant")
                found = true
                break
            } catch (_: AssertionError) {
                Log.w("MainMenuScreenTest", "How to play variant not found: $variant")
            }
        }
        if (!found) {
            Log.e("MainMenuScreenTest", "No How to play button found in any variant")
            takeScreenshot(device, "no_how_to_play")
        }
        assert(found) { "No se encontró el botón de cómo se juega en español o inglés" }
    }

    @Test
    fun mainMenu_hasAnyTextNode() {
        // Verifies that at least one text node exists in the main menu
        val allTextNodes = composeTestRule.onAllNodes(hasText("", substring = true))
        val count = allTextNodes.fetchSemanticsNodes().size
        Log.d("MainMenuScreenTest", "Text node count in main menu: $count")
        assert(count > 0) { "No text nodes found in main menu UI" }
    }
}
