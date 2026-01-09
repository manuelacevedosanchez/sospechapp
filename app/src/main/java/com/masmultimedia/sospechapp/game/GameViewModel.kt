package com.masmultimedia.sospechapp.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.masmultimedia.sospechapp.R
import com.masmultimedia.sospechapp.core.text.UiText
import com.masmultimedia.sospechapp.words.data.AssetsWordsRepository
import com.masmultimedia.sospechapp.words.domain.WordsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<GameEffect>()
    val effect: SharedFlow<GameEffect> = _effect.asSharedFlow()

    // Repo local (assets) to run now
    private val wordsRepository: WordsRepository =
        AssetsWordsRepository(context = application.applicationContext)

    fun onAction(action: GameAction) {
        when (action) {
            is GameAction.StartGame -> startGame(
                action.totalPlayers,
                action.impostors,
                action.wordInput
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

    private fun startGame(totalPlayers: Int, impostors: Int, wordInput: String?) {
        if (totalPlayers < 3 || impostors < 1 || impostors >= totalPlayers) {
            sendError(UiText.TextResource(R.string.error_invalid_players_impostors))
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Try to sync words before starting the game, if don't have red or api = null will use local data
            // Not using by now, only assets
            wordsRepository.syncIfNeeded()

            val finalWord = wordInput?.takeIf { it.isNotBlank() } ?: wordsRepository.getRandomWord()
            val generatedRoles = generateRoles(totalPlayers, impostors)

            _uiState.update {
                it.copy(
                    totalPlayers = totalPlayers,
                    impostors = impostors,
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

            viewModelScope.launch {
                _effect.emit(GameEffect.NavigateToReadyToPlay)
            }
        }
    }

    private fun resetGame() {
        _uiState.update {
            GameState()
        }
    }

    private fun sendError(message: UiText) {
        _uiState.update { it.copy(errorMessage = message) }

        viewModelScope.launch {
            _effect.emit(GameEffect.ShowError(message))
        }
    }

}