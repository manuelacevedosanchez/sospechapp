package com.masmultimedia.sospechapp.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.masmultimedia.sospechapp.R
import com.masmultimedia.sospechapp.words.data.AssetsWordsRepository
import com.masmultimedia.sospechapp.words.data.prefs.CategoryHistoryPrefs
import com.masmultimedia.sospechapp.words.domain.WordsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface StringProvider {
    fun getString(resId: Int): String
}

class AndroidStringProvider(private val context: android.content.Context) : StringProvider {
    override fun getString(resId: Int): String = context.getString(resId)
}


class GameViewModel(
    application: Application,
    private val stringProvider: StringProvider = AndroidStringProvider(application.applicationContext),
    private val wordsRepository: WordsRepository = AssetsWordsRepository(context = application.applicationContext),
    private val categoryHistoryPrefs: CategoryHistoryPrefs = CategoryHistoryPrefs(application.applicationContext),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : AndroidViewModel(application) {
    /**
     * Advances to the next round or navigates to voting if it was the last round.
     */
    fun nextRound() {
        val state = _uiState.value
        if (state.currentRound < state.rounds) {
            _uiState.update { it.copy(currentRound = it.currentRound + 1) }
            viewModelScope.launch(dispatcher) { _effect.emit(GameEffect.NavigateToRound) }
        } else {
            // Last round, go to voting
            viewModelScope.launch(dispatcher) { _effect.emit(GameEffect.NavigateToVote) }
        }
    }
    /**
     * Clears the persisted category and recent words history.
     */
    fun clearHistory() {
        viewModelScope.launch(dispatcher) {
            categoryHistoryPrefs.clearHistory()
        }
    }

    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<GameEffect>()
    val effect: SharedFlow<GameEffect> = _effect.asSharedFlow()

    fun onAction(action: GameAction) {
        when (action) {
            is GameAction.StartGame -> startGame(
                action.totalPlayers,
                action.impostors,
                action.rounds,
                action.wordInput,
                action.category,
                action.difficulty
            )

            is GameAction.SetHapticsEnabled -> {
                _uiState.update { it.copy(settings = it.settings.copy(hapticsEnabled = action.enabled)) }
            }

            is GameAction.SetAnimationsEnabled -> {
                _uiState.update { it.copy(settings = it.settings.copy(animationsEnabled = action.enabled)) }
            }

            is GameAction.SetKeepScreenOn -> {
                _uiState.update { it.copy(settings = it.settings.copy(keepScreenOn = action.enabled)) }
            }


            GameAction.RevealRole -> revealRole()
            GameAction.HideRoleAndNext -> hideRoleAndNext()
            GameAction.ResetGame -> resetGame()
        }
    }

    // Update startGame to accept category and difficulty
    private fun startGame(
        totalPlayers: Int,
        impostors: Int,
        rounds: Int,
        wordInput: String?,
        category: String?,
        difficulty: String?
    ) {
        if (totalPlayers < 3 || impostors < 1 || impostors >= totalPlayers) {
            sendError(stringProvider.getString(R.string.error_invalid_players))
            return
        }
        viewModelScope.launch(dispatcher) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            wordsRepository.syncIfNeeded()
            val chosenCategory = pickCategory(category)
            val finalWord =
                wordInput?.takeIf { it.isNotBlank() } ?: pickWord(chosenCategory, difficulty)
            val generatedRoles = generateRoles(totalPlayers, impostors)
            _uiState.update {
                it.copy(
                    totalPlayers = totalPlayers,
                    impostors = impostors,
                    rounds = rounds,
                    currentRound = 1,
                    wordInput = wordInput.orEmpty(),
                    currentWord = finalWord,
                    roles = generatedRoles,
                    currentPlayerIndex = 0,
                    isRoleVisible = false,
                    isGameStarted = true,
                    isReadyToPlay = false,
                    isLoading = false,
                    errorMessage = null
                )
            }
            // Persist category and word
            chosenCategory?.let { categoryHistoryPrefs.setLastCategory(it) }
            categoryHistoryPrefs.addRecentWord(finalWord)
            _effect.emit(GameEffect.NavigateToRevealRoles)
        }
    }

    private fun generateRoles(totalPlayers: Int, impostors: Int): List<PlayerRole> {
        val roles = mutableListOf<PlayerRole>()
        repeat(impostors) {
            roles.add(PlayerRole.IMPOSTOR)
        }
        repeat(totalPlayers - impostors) {
            roles.add(PlayerRole.CITIZEN)
        }
        roles.shuffle()
        return roles
    }

    private fun revealRole() {
        _uiState.update { state ->
            if (!state.isGameStarted) {
                state
            } else {
                state.copy(isRoleVisible = true)
            }
        }
    }

    private fun hideRoleAndNext() {
        val currentState = _uiState.value
        if (!currentState.isGameStarted) return

        val isLastPlayer = currentState.currentPlayerIndex >= currentState.totalPlayers - 1

        if (!isLastPlayer) {
            // Move to next player and hide role
            _uiState.update { state ->
                state.copy(
                    currentPlayerIndex = state.currentPlayerIndex + 1,
                    isRoleVisible = false
                )
            }
        } else {
            // All players have seen their roles, ready to play
            _uiState.update { state ->
                state.copy(
                    isReadyToPlay = true,
                    isRoleVisible = false
                )
            }

            viewModelScope.launch(dispatcher) {
                _effect.emit(GameEffect.NavigateToReadyToPlay)
            }
        }
    }

    private fun resetGame() {
        _uiState.update {
            GameState()
        }
    }

    private fun sendError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }

        viewModelScope.launch(dispatcher) {
            _effect.emit(GameEffect.ShowError(message))
        }
    }

    private suspend fun pickCategory(requested: String?): String? {
        val categories = getAllCategories()
        val lastCategory = categoryHistoryPrefs.getLastCategory()
        val filtered = categories.filter { it != lastCategory }
        return requested ?: filtered.randomOrNull() ?: categories.randomOrNull()
    }

    private suspend fun pickWord(category: String?, difficulty: String?): String {
        val recent = categoryHistoryPrefs.getRecentWords()
        val words = getWordsFiltered(category, difficulty)
        val filtered = words.filter { it !in recent }
        return (filtered.ifEmpty { words }).random()
    }

    private suspend fun getWordsFiltered(category: String?, difficulty: String?): List<String> {
        // Use repository to get all possible words for the filter
        return (wordsRepository as? AssetsWordsRepository)?.let { repo ->
            val all = repo.run {
                val words = cachedWords ?: loadWordsSafely().also { cachedWords = it }
                words.filter {
                    (category == null || it.category?.trim()
                        .equals(category.trim(), ignoreCase = true)) &&
                            (difficulty == null || it.difficulty?.trim()
                                .equals(difficulty.trim(), ignoreCase = true))
                }.map { it.text }
            }
            all.ifEmpty { listOf(repo.fallBackWords.random()) }
        } ?: listOf(wordsRepository.getRandomWord(category, difficulty))
    }

    private fun getAllCategories(): List<String> {
        return (wordsRepository as? AssetsWordsRepository)?.let { repo ->
            val words = repo.run { cachedWords ?: loadWordsSafely() }
            words.mapNotNull { it.category }.distinct()
        } ?: listOf("comida", "objetos", "personajes", "animales", "lugares")
    }

}