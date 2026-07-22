package com.masmultimedia.sospechapp

import android.util.Log
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import androidx.compose.ui.test.junit4.createComposeRule
import com.masmultimedia.sospechapp.ui.HowToPlayScreen
import com.masmultimedia.sospechapp.R
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import com.masmultimedia.sospechapp.ui.components.LocalSospechSnackbarHostState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@RunWith(AndroidJUnit4::class)
class HowToPlayScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun takeScreenshot(device: UiDevice, tag: String) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "/sdcard/sospechapp_test_${tag}_$timestamp.png"
        val success = device.takeScreenshot(File(fileName))
        Log.d("HowToPlayScreenTest", "Screenshot taken: $fileName, success: $success")
    }

    @Test
    fun howToPlay_showsInstructions() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        // Wait for the app to be ready
        device.wait(Until.hasObject(By.pkg(context.packageName)), 3000)
        Log.d("HowToPlayScreenTest", "App package loaded: ${context.packageName}")
        // Try clicking the "How to play" button in Spanish or English
        val howToPlayVariants = listOf("Cómo se juega", "Como se juega", "How to play")
        var clicked = false
        for (variant in howToPlayVariants) {
            val obj = device.findObject(By.textContains(variant))
            if (obj != null) {
                Log.d("HowToPlayScreenTest", "Clicking button variant: $variant")
                obj.click()
                clicked = true
                break
            }
        }
        if (!clicked) {
            Log.e("HowToPlayScreenTest", "No 'How to play' button found in any variant")
            takeScreenshot(device, "no_how_to_play_button")
        }
        // Validate the screen title and subtitle in Spanish or English
        val titleVariants = listOf("Cómo se juega", "Como se juega", "How to play")
        val subtitleVariants = listOf("Reglas rápidas", "Reglas rapidas", "Quick rules")
        var foundTitle = false
        for (variant in titleVariants) {
            try {
                composeTestRule.onNodeWithText(variant, substring = true).assertExists()
                Log.d("HowToPlayScreenTest", "Found title variant: $variant")
                foundTitle = true
                break
            } catch (_: AssertionError) {
                Log.w("HowToPlayScreenTest", "Title variant not found: $variant")
            }
        }
        if (!foundTitle) {
            Log.e("HowToPlayScreenTest", "No title found in any variant")
            takeScreenshot(device, "no_title")
        }
        assert(foundTitle) { "No se encontró el título de la pantalla en ningún idioma/variante" }
        var foundSubtitle = false
        for (variant in subtitleVariants) {
            try {
                composeTestRule.onNodeWithText(variant, substring = true).assertExists()
                Log.d("HowToPlayScreenTest", "Found subtitle variant: $variant")
                foundSubtitle = true
                break
            } catch (_: AssertionError) {
                Log.w("HowToPlayScreenTest", "Subtitle variant not found: $variant")
            }
        }
        if (!foundSubtitle) {
            Log.e("HowToPlayScreenTest", "No subtitle found in any variant")
            takeScreenshot(device, "no_subtitle")
        }
        assert(foundSubtitle) { "No se encontró el subtítulo de la pantalla en ningún idioma/variante" }
    }
}

class HowToPlayScreenIsolatedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun howToPlayScreen_displaysAllTexts() {
        composeTestRule.setContent {
            val snackbarHostState = SnackbarHostState()
            CompositionLocalProvider(LocalSospechSnackbarHostState provides snackbarHostState) {
                HowToPlayScreen(onBackClick = {})
            }
        }
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        // Check for title and subtitle from resources
        composeTestRule.onNodeWithText(context.getString(R.string.how_to_play_title)).assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.how_to_play_subtitle)).assertExists()
        // The active locale selects either values or values-en; assert the same resource rendered by the UI.
        composeTestRule.onNodeWithText(context.getString(R.string.how_to_play_steps)).assertExists()
    }
}
