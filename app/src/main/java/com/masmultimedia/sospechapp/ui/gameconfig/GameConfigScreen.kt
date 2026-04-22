package com.masmultimedia.sospechapp.ui.gameconfig

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.masmultimedia.sospechapp.R
import com.masmultimedia.sospechapp.ui.components.PrimaryButton
import com.masmultimedia.sospechapp.ui.components.SecondaryButton
import com.masmultimedia.sospechapp.ui.components.SospechCard
import com.masmultimedia.sospechapp.ui.components.SospechScaffold
import com.masmultimedia.sospechapp.ui.components.SospechTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameConfigScreen(
    onBackClick: () -> Unit,
    onStartGame: (totalPlayers: Int, impostors: Int, word: String?, category: String?, difficulty: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var totalPlayers by remember { mutableIntStateOf(5) }
    var impostors by remember { mutableIntStateOf(1) }
    var wordInput by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val categoryOptions = listOf(
        null, // All categories
        "comida",
        "objetos",
        "personajes",
        "animales",
        "lugares",
        "naturaleza"
    )
    var expandedDifficulty by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf<String?>(null) }
    val difficultyOptions = listOf(
        null, // All difficulties
        "easy",
        "medium",
        "hard"
    )
    val safeTotalPlayers = totalPlayers.coerceAtLeast(3)
    val safeImpostors = impostors.coerceIn(1, safeTotalPlayers - 1)
    val isStartEnabled = safeTotalPlayers >= 3 && safeImpostors in 1..<safeTotalPlayers
    SospechScaffold(
        topBar = {
            SospechTopBar(
                title = stringResource(R.string.config_title),
                subtitle = stringResource(R.string.config_subtitle),
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.2f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SospechCard(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.config_players),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        StepperRow(
                            value = totalPlayers,
                            onMinus = { if (totalPlayers > 3) totalPlayers-- },
                            onPlus = { totalPlayers++ }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.config_minimum_3),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    SospechCard(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.config_impostors),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        StepperRow(
                            value = impostors,
                            onMinus = { if (impostors > 1) impostors-- },
                            onPlus = { if (impostors < totalPlayers - 1) impostors++ }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(
                                R.string.config_impostors_range,
                                totalPlayers - 1
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                SospechCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.8f)
                ) {
                    OutlinedTextField(
                        value = wordInput,
                        onValueChange = { wordInput = it },
                        label = { Text(stringResource(R.string.config_word)) },
                        placeholder = { Text(stringResource(R.string.config_word_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SospechCard(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.config_category),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        ExposedDropdownMenuBox(
                            expanded = expandedCategory,
                            onExpandedChange = { expandedCategory = !expandedCategory }
                        ) {
                            OutlinedTextField(
                                value = selectedCategory
                                    ?: stringResource(R.string.config_category_hint),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.config_category)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedCategory,
                                onDismissRequest = { expandedCategory = false }
                            ) {
                                categoryOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                option
                                                    ?: stringResource(R.string.config_category_hint)
                                            )
                                        },
                                        onClick = {
                                            selectedCategory = option
                                            expandedCategory = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    SospechCard(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.config_difficulty),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        ExposedDropdownMenuBox(
                            expanded = expandedDifficulty,
                            onExpandedChange = { expandedDifficulty = !expandedDifficulty }
                        ) {
                            OutlinedTextField(
                                value = selectedDifficulty
                                    ?: stringResource(R.string.config_difficulty_hint),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.config_difficulty)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDifficulty) },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedDifficulty,
                                onDismissRequest = { expandedDifficulty = false }
                            ) {
                                difficultyOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                option
                                                    ?: stringResource(R.string.config_difficulty_hint)
                                            )
                                        },
                                        onClick = {
                                            selectedDifficulty = option
                                            expandedDifficulty = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(0.2f))
                PrimaryButton(
                    text = stringResource(R.string.config_start),
                    onClick = {
                        val cleanedWord: String? = wordInput.trim().ifBlank { null }
                        onStartGame(
                            safeTotalPlayers,
                            safeImpostors,
                            cleanedWord,
                            selectedCategory,
                            selectedDifficulty
                        )
                    },
                    enabled = isStartEnabled,
                    modifier = Modifier.fillMaxWidth()
                )
                SecondaryButton(
                    text = stringResource(R.string.config_back),
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun StepperRow(
    value: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = onMinus,
            modifier = Modifier.size(40.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Remove,
                contentDescription = stringResource(R.string.stepper_decrease)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.widthIn(min = 32.dp, max = 48.dp),
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(16.dp))
        OutlinedButton(
            onClick = onPlus,
            modifier = Modifier.size(40.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = stringResource(R.string.stepper_increase)
            )
        }
    }
}