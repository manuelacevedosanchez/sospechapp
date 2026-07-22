package com.masmultimedia.sospechapp.ui.gameconfig

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.masmultimedia.sospechapp.R
import com.masmultimedia.sospechapp.game.GameAction
import com.masmultimedia.sospechapp.game.GameState
import com.masmultimedia.sospechapp.ui.components.PrimaryButton
import com.masmultimedia.sospechapp.ui.components.SospechCard
import com.masmultimedia.sospechapp.ui.components.SospechScaffold
import com.masmultimedia.sospechapp.ui.components.SospechTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameConfigScreen(
    state: GameState,
    onBackClick: () -> Unit,
    onAction: (GameAction) -> Unit,
    onStartGame: (totalPlayers: Int, impostors: Int, rounds: Int, word: String?, category: String?, difficulty: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var totalPlayers by rememberSaveable { mutableIntStateOf(5) }
    var impostors by rememberSaveable { mutableIntStateOf(1) }
    var rounds by rememberSaveable { mutableIntStateOf(1) }
    var expandedCategory by remember { mutableStateOf(false) }
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }
    var expandedDifficulty by remember { mutableStateOf(false) }
    var selectedDifficulty by rememberSaveable { mutableStateOf<String?>(null) }

    val categoryOptions = listOf(
        null to stringResource(R.string.config_category_hint),
        "comida" to stringResource(R.string.category_comida),
        "objetos" to stringResource(R.string.category_objetos),
        "personajes" to stringResource(R.string.category_personajes),
        "animales" to stringResource(R.string.category_animales),
        "lugares" to stringResource(R.string.category_lugares),
        "naturaleza" to stringResource(R.string.category_naturaleza),
    )
    val difficultyOptions = listOf(
        null to stringResource(R.string.config_difficulty_hint),
        "easy" to stringResource(R.string.difficulty_easy),
        "medium" to stringResource(R.string.difficulty_medium),
        "hard" to stringResource(R.string.difficulty_hard),
    )
    val filtersEnabled = !state.useCustomWord

    SospechScaffold(
        topBar = {
            SospechTopBar(
                title = stringResource(R.string.config_title),
                subtitle = stringResource(R.string.config_subtitle),
                onBackClick = onBackClick,
            )
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)) {
                PrimaryButton(
                    text = stringResource(R.string.config_start),
                    onClick = {
                        onStartGame(
                            totalPlayers,
                            impostors,
                            rounds,
                            state.wordInput.trim().ifBlank { null },
                            selectedCategory.takeIf { filtersEnabled },
                            selectedDifficulty.takeIf { filtersEnabled },
                        )
                    },
                    enabled = !state.isLoading && totalPlayers >= 3 && impostors in 1 until totalPlayers && rounds >= 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .testTag(START_BUTTON_TAG),
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag(CONFIG_LIST_TAG),
            contentPadding = PaddingValues(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                SospechCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.config_custom_word_toggle),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = stringResource(R.string.config_custom_word_explanation),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                            )
                        }
                        Switch(
                            checked = state.useCustomWord,
                            onCheckedChange = { onAction(GameAction.SetCustomWordMode(it)) },
                            modifier = Modifier.testTag(CUSTOM_WORD_SWITCH_TAG),
                        )
                    }
                    if (state.useCustomWord) {
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = state.wordInput,
                            onValueChange = { onAction(GameAction.SetCustomWord(it)) },
                            label = { Text(stringResource(R.string.config_word)) },
                            placeholder = { Text(stringResource(R.string.config_custom_word_placeholder)) },
                            supportingText = if (state.customWordError) {
                                { Text(stringResource(R.string.error_custom_word_required)) }
                            } else null,
                            isError = state.customWordError,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag(CUSTOM_WORD_FIELD_TAG),
                        )
                    }
                }
            }

            item {
                FilterSelector(
                    title = stringResource(R.string.config_category),
                    value = categoryOptions.first { it.first == selectedCategory }.second,
                    options = categoryOptions,
                    expanded = expandedCategory,
                    enabled = filtersEnabled,
                    onExpandedChange = { expandedCategory = it },
                    onSelected = { selectedCategory = it },
                    testTag = CATEGORY_TAG,
                )
            }

            item {
                FilterSelector(
                    title = stringResource(R.string.config_difficulty),
                    value = difficultyOptions.first { it.first == selectedDifficulty }.second,
                    options = difficultyOptions,
                    expanded = expandedDifficulty,
                    enabled = filtersEnabled,
                    onExpandedChange = { expandedDifficulty = it },
                    onSelected = { selectedDifficulty = it },
                    testTag = DIFFICULTY_TAG,
                )
            }

            item {
                SospechCard {
                    ConfigStepper(
                        title = stringResource(R.string.config_players),
                        supportingText = stringResource(R.string.config_minimum_3),
                        value = totalPlayers,
                        canDecrease = totalPlayers > 3,
                        onDecrease = {
                            totalPlayers--
                            impostors = impostors.coerceAtMost(totalPlayers - 1)
                        },
                        onIncrease = { totalPlayers++ },
                    )
                    Spacer(Modifier.height(12.dp))
                    ConfigStepper(
                        title = stringResource(R.string.config_impostors),
                        supportingText = stringResource(R.string.config_impostors_range, totalPlayers - 1),
                        value = impostors,
                        canDecrease = impostors > 1,
                        canIncrease = impostors < totalPlayers - 1,
                        onDecrease = { impostors-- },
                        onIncrease = { impostors++ },
                    )
                    Spacer(Modifier.height(12.dp))
                    ConfigStepper(
                        title = stringResource(R.string.config_rounds),
                        supportingText = stringResource(R.string.config_rounds_minimum),
                        value = rounds,
                        canDecrease = rounds > 1,
                        onDecrease = { rounds-- },
                        onIncrease = { rounds++ },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSelector(
    title: String,
    value: String,
    options: List<Pair<String?, String>>,
    expanded: Boolean,
    enabled: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelected: (String?) -> Unit,
    testTag: String,
) {
    SospechCard {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (enabled) onExpandedChange(!expanded) },
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                enabled = enabled,
                label = { Text(title) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .testTag(testTag),
            )
            ExposedDropdownMenu(
                expanded = expanded && enabled,
                onDismissRequest = { onExpandedChange(false) },
            ) {
                options.forEach { (id, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            onSelected(id)
                            onExpandedChange(false)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfigStepper(
    title: String,
    supportingText: String,
    value: Int,
    canDecrease: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    canIncrease: Boolean = true,
) {
    Text(text = title, style = MaterialTheme.typography.labelLarge)
    StepperRow(value, onDecrease, onIncrease, canDecrease, canIncrease)
    Text(
        text = supportingText,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
    )
}

@Composable
private fun StepperRow(
    value: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    canDecrease: Boolean,
    canIncrease: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedButton(
            onClick = onMinus,
            enabled = canDecrease,
            modifier = Modifier.size(48.dp),
            contentPadding = PaddingValues(0.dp),
        ) {
            Icon(Icons.Rounded.Remove, stringResource(R.string.stepper_decrease))
        }
        Spacer(Modifier.width(16.dp))
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.widthIn(min = 32.dp, max = 48.dp),
            maxLines = 1,
        )
        Spacer(Modifier.width(16.dp))
        OutlinedButton(
            onClick = onPlus,
            enabled = canIncrease,
            modifier = Modifier.size(48.dp),
            contentPadding = PaddingValues(0.dp),
        ) {
            Icon(Icons.Rounded.Add, stringResource(R.string.stepper_increase))
        }
    }
}

internal const val CONFIG_LIST_TAG = "game_config_list"
internal const val START_BUTTON_TAG = "game_config_start"
internal const val CUSTOM_WORD_SWITCH_TAG = "custom_word_switch"
internal const val CUSTOM_WORD_FIELD_TAG = "custom_word_field"
internal const val CATEGORY_TAG = "category_selector"
internal const val DIFFICULTY_TAG = "difficulty_selector"
