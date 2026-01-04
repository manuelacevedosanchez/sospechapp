package com.masmultimedia.sospechapp.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<GameEffect>()
    val effect: SharedFlow<GameEffect> = _effect.asSharedFlow()

    // Temporary random words list. Later this will come from data layer
    private val randomWords = listOf(
        "playa",
        "montaña",
        "pizza",
        "castillo",
        "robot",
        "volcán",
        "guitarra",
        "zombi",
        "biblioteca",
        "cohete"
    )

    fun onAction(action: GameAction) {
        when (action) {
            is GameAction.StartGame -> startGame(
                action.totalPlayers,
                action.impostors,
                action.wordInput
            )

            GameAction.RevealRole -> revealRole()
            GameAction.HideRoleAndNext -> hideRoleAndNext()
            GameAction.ResetGame -> resetGame()
        }
    }

    private fun startGame(totalPlayers: Int, impostors: Int, wordInput: String?) {
        if (totalPlayers < 3 || impostors < 1 || impostors >= totalPlayers) {
            sendError("Número de jugadores o impostores inválido")
            return
        }

        val finalWord = wordInput?.takeIf { it.isNotBlank() } ?: getRandomWord()
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

        viewModelScope.launch {
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

    private fun getRandomWord(): String {
        return randomWords.random()
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

    private fun sendError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }

        viewModelScope.launch {
            _effect.emit(GameEffect.ShowError(message))
        }
    }

}