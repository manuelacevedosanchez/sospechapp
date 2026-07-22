package com.masmultimedia.sospechapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.masmultimedia.sospechapp.game.GameAction
import com.masmultimedia.sospechapp.game.GameState
import com.masmultimedia.sospechapp.ui.gameconfig.CATEGORY_TAG
import com.masmultimedia.sospechapp.ui.gameconfig.CONFIG_LIST_TAG
import com.masmultimedia.sospechapp.ui.gameconfig.CUSTOM_WORD_FIELD_TAG
import com.masmultimedia.sospechapp.ui.gameconfig.CUSTOM_WORD_SWITCH_TAG
import com.masmultimedia.sospechapp.ui.gameconfig.DIFFICULTY_TAG
import com.masmultimedia.sospechapp.ui.gameconfig.GameConfigScreen
import com.masmultimedia.sospechapp.ui.gameconfig.START_BUTTON_TAG
import com.masmultimedia.sospechapp.ui.theme.SospechAppTheme
import org.junit.Rule
import org.junit.Test

class GameConfigScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun filtersAreReachableAndStartButtonStaysVisible() {
        composeRule.setContent {
            SospechAppTheme {
                GameConfigScreen(
                    state = GameState(),
                    onBackClick = {},
                    onAction = {},
                    onStartGame = { _, _, _, _, _, _ -> },
                )
            }
        }

        composeRule.onNodeWithTag(START_BUTTON_TAG).assertIsDisplayed().assertIsEnabled()
        composeRule.onNodeWithTag(CONFIG_LIST_TAG).performScrollToNode(hasTestTag(DIFFICULTY_TAG))
        composeRule.onNodeWithTag(CATEGORY_TAG).assertExists()
        composeRule.onNodeWithTag(DIFFICULTY_TAG).assertIsDisplayed()
    }

    @Test
    fun customWordModeShowsFieldAndDisablesFilters() {
        var state by mutableStateOf(GameState())
        composeRule.setContent {
            SospechAppTheme {
                GameConfigScreen(
                    state = state,
                    onBackClick = {},
                    onAction = { action ->
                        when (action) {
                            is GameAction.SetCustomWordMode -> state = state.copy(useCustomWord = action.enabled)
                            is GameAction.SetCustomWord -> state = state.copy(wordInput = action.word)
                            else -> Unit
                        }
                    },
                    onStartGame = { _, _, _, _, _, _ -> },
                )
            }
        }

        composeRule.onNodeWithTag(CUSTOM_WORD_SWITCH_TAG).performClick()
        composeRule.onNodeWithTag(CUSTOM_WORD_FIELD_TAG).assertIsDisplayed()
        composeRule.onNodeWithTag(CATEGORY_TAG).assertIsNotEnabled()
        composeRule.onNodeWithTag(DIFFICULTY_TAG).assertIsNotEnabled()
        composeRule.onNodeWithTag(START_BUTTON_TAG).assertIsDisplayed()
    }
}
