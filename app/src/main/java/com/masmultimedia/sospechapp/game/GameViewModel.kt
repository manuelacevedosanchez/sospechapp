package com.masmultimedia.sospechapp.game

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.masmultimedia.sospechapp.R
import com.masmultimedia.sospechapp.words.data.AssetsWordsRepository
import com.masmultimedia.sospechapp.words.data.prefs.CategoryHistoryPrefs
import com.masmultimedia.sospechapp.words.domain.WordsRepository
import com.masmultimedia.sospechapp.words.domain.WordsResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.ceil
import kotlin.random.Random

interface StringProvider {
    fun getString(resId: Int): String
}

class AndroidStringProvider(private val context: android.content.Context) : StringProvider {
    override fun getString(resId: Int): String = context.getString(resId)
}

class GameViewModel(
    application: Application,
    private val stringProvider: StringProvider = AndroidStringProvider(application.applicationContext),
    private val wordsRepository: WordsRepository = AssetsWordsRepository(application.applicationContext),
    private val categoryHistoryPrefs: CategoryHistoryPrefs = CategoryHistoryPrefs(application.applicationContext),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val random: Random = Random.Default,
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private val _effect = MutableSharedFlow<GameEffect>()
    val effect: SharedFlow<GameEffect> = _effect.asSharedFlow()

    private var startGameJob: Job? = null
    private var roundTransitionJob: Job? = null

    fun onAction(action: GameAction) {
        when (action) {
            is GameAction.StartGame -> startGame(action)
            is GameAction.SetCustomWordMode -> updateConfiguration {
                it.copy(useCustomWord = action.enabled, customWordError = false, errorMessage = null)
            }
            is GameAction.SetCustomWord -> updateConfiguration {
                it.copy(wordInput = action.word, customWordError = false, errorMessage = null)
            }
            is GameAction.SetHapticsEnabled -> _settings.update { it.copy(hapticsEnabled = action.enabled) }
            is GameAction.SetAnimationsEnabled -> _settings.update { it.copy(animationsEnabled = action.enabled) }
            is GameAction.SetKeepScreenOn -> _settings.update { it.copy(keepScreenOn = action.enabled) }
            GameAction.RevealRole -> revealRole()
            GameAction.HideRoleAndNext -> hideRoleAndNext()
            GameAction.StartRounds -> startRounds()
            GameAction.FinishRound -> finishRound()
            GameAction.CancelStartGame -> cancelStartGame()
            GameAction.ResetGame -> resetGame()
        }
    }

    fun clearHistory() {
        viewModelScope.launch(dispatcher) { categoryHistoryPrefs.clearHistory() }
    }

    private fun updateConfiguration(transform: (GameState) -> GameState) {
        _uiState.update { state ->
            if (state.phase == GamePhase.CONFIGURATION) transform(state) else state
        }
    }

    private fun startGame(action: GameAction.StartGame) {
        if (_uiState.value.phase != GamePhase.CONFIGURATION) return
        if (action.totalPlayers < 3 || action.impostors !in 1 until action.totalPlayers || action.rounds < 1) {
            sendError(stringProvider.getString(R.string.error_invalid_players))
            return
        }
        val customWord = action.wordInput?.trim().orEmpty()
        if (action.useCustomWord && customWord.isBlank()) {
            _uiState.update { it.copy(customWordError = true) }
            sendError(stringProvider.getString(R.string.error_custom_word_required))
            return
        }

        startGameJob?.cancel()
        _uiState.update { it.copy(phase = GamePhase.LOADING, errorMessage = null, customWordError = false) }
        val job = viewModelScope.launch(dispatcher) {
            try {
                val selection = if (action.useCustomWord) {
                    SelectedWord(customWord, usedFallback = false)
                } else {
                    wordsRepository.syncIfNeeded()
                    selectCatalogWord(action.category, action.difficulty)
                }
                currentCoroutineContext().ensureActive()
                val generatedRoles = generateRoles(action.totalPlayers, action.impostors)
                _uiState.update {
                    it.copy(
                        phase = GamePhase.REVEALING_ROLES,
                        totalPlayers = action.totalPlayers,
                        impostors = action.impostors,
                        rounds = action.rounds,
                        currentRound = 1,
                        wordInput = if (action.useCustomWord) customWord else "",
                        currentWord = selection.text,
                        roles = generatedRoles,
                        currentPlayerIndex = 0,
                        isRoleVisible = false,
                        errorMessage = null,
                        useCustomWord = action.useCustomWord,
                        isUsingFallback = selection.usedFallback,
                    )
                }
                currentCoroutineContext().ensureActive()
                _effect.emit(GameEffect.NavigateToRevealRoles)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                Log.e(TAG, "Unable to start game", error)
                _uiState.update { it.copy(phase = GamePhase.CONFIGURATION) }
                sendError(stringProvider.getString(R.string.error_word_catalog_unavailable))
            }
        }
        startGameJob = job
        job.invokeOnCompletion { if (startGameJob === job) startGameJob = null }
    }

    private suspend fun selectCatalogWord(category: String?, difficulty: String?): SelectedWord {
        val result = wordsRepository.getWords(category, difficulty)
        val source = when (result) {
            is WordsResult.Success -> "catalog"
            is WordsResult.Fallback -> "fallback"
            WordsResult.Empty -> throw IllegalStateException("No words for category=$category difficulty=$difficulty")
            is WordsResult.Error -> throw result.cause
        }
        var candidates = result.words
        val normalizedCategory = category?.trim()?.lowercase(Locale.ROOT)
        val chosenCategory = normalizedCategory ?: candidates.map { it.category }.distinct().random(random)
        candidates = candidates.filter { it.category == chosenCategory }
        if (candidates.isEmpty()) throw IllegalStateException("Selected category has no words: $chosenCategory")

        val historyKey = historyKey(source, chosenCategory, difficulty)
        val candidateIds = candidates.mapTo(mutableSetOf()) { it.id }
        var usedIds = categoryHistoryPrefs.getUsedWordIds(historyKey).intersect(candidateIds)
        val resetThreshold = ceil(candidates.size * HISTORY_RESET_RATIO).toInt().coerceAtLeast(1)
        if (usedIds.size >= resetThreshold) {
            categoryHistoryPrefs.clearWordHistory(historyKey)
            usedIds = emptySet()
        }
        val word = candidates.filterNot { it.id in usedIds }.ifEmpty { candidates }.random(random)
        categoryHistoryPrefs.addUsedWordId(historyKey, word.id)
        categoryHistoryPrefs.setLastCategory(chosenCategory)
        return SelectedWord(word.text, usedFallback = result is WordsResult.Fallback)
    }

    private fun generateRoles(totalPlayers: Int, impostors: Int): List<PlayerRole> =
        (MutableList(impostors) { PlayerRole.IMPOSTOR } +
            MutableList(totalPlayers - impostors) { PlayerRole.CITIZEN }).shuffled(random)

    private fun revealRole() {
        _uiState.update { state ->
            if (state.phase == GamePhase.REVEALING_ROLES) state.copy(isRoleVisible = true) else state
        }
    }

    private fun hideRoleAndNext() {
        val state = _uiState.value
        if (state.phase != GamePhase.REVEALING_ROLES || !state.isRoleVisible) return
        if (state.currentPlayerIndex < state.totalPlayers - 1) {
            _uiState.update { it.copy(currentPlayerIndex = it.currentPlayerIndex + 1, isRoleVisible = false) }
        } else {
            _uiState.update { it.copy(phase = GamePhase.READY, isRoleVisible = false) }
            viewModelScope.launch(dispatcher) { _effect.emit(GameEffect.NavigateToReadyToPlay) }
        }
    }

    private fun startRounds() {
        if (_uiState.value.phase != GamePhase.READY) return
        _uiState.update { it.copy(phase = GamePhase.PLAYING_ROUNDS) }
        viewModelScope.launch(dispatcher) { _effect.emit(GameEffect.NavigateToRound) }
    }

    private fun finishRound() {
        val state = _uiState.value
        if (state.phase != GamePhase.PLAYING_ROUNDS) return
        if (state.currentRound < state.rounds) {
            _uiState.update {
                it.copy(phase = GamePhase.ADVANCING_ROUND, currentRound = it.currentRound + 1)
            }
            roundTransitionJob?.cancel()
            val job = viewModelScope.launch(dispatcher) {
                delay(ROUND_TRANSITION_DELAY_MS)
                _uiState.update {
                    if (it.phase == GamePhase.ADVANCING_ROUND) it.copy(phase = GamePhase.PLAYING_ROUNDS) else it
                }
            }
            roundTransitionJob = job
            job.invokeOnCompletion { if (roundTransitionJob === job) roundTransitionJob = null }
        } else {
            _uiState.update { it.copy(phase = GamePhase.VOTING) }
            viewModelScope.launch(dispatcher) { _effect.emit(GameEffect.NavigateToVote) }
        }
    }

    private fun cancelStartGame() {
        startGameJob?.cancel()
        startGameJob = null
        roundTransitionJob?.cancel()
        roundTransitionJob = null
        _uiState.value = GameState()
    }

    private fun resetGame() {
        cancelStartGame()
    }

    private fun sendError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
        viewModelScope.launch(dispatcher) { _effect.emit(GameEffect.ShowError(message)) }
    }

    private fun historyKey(source: String, category: String, difficulty: String?): String =
        "$source|${category.trim().lowercase(Locale.ROOT)}|${difficulty?.trim()?.lowercase(Locale.ROOT) ?: "all"}"

    private data class SelectedWord(val text: String, val usedFallback: Boolean)

    private companion object {
        const val TAG = "GameViewModel"
        const val HISTORY_RESET_RATIO = 0.75
        const val ROUND_TRANSITION_DELAY_MS = 300L
    }
}
