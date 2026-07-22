package com.masmultimedia.sospechapp.game

data class GameState(
    val phase: GamePhase = GamePhase.CONFIGURATION,
    val totalPlayers: Int = 0,
    val impostors: Int = 0,
    val rounds: Int = 1,
    val currentRound: Int = 1,
    val wordInput: String = "",
    val currentWord: String? = null,
    val roles: List<PlayerRole> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val isRoleVisible: Boolean = false,
    val errorMessage: String? = null,
    val useCustomWord: Boolean = false,
    val customWordError: Boolean = false,
    val isUsingFallback: Boolean = false,
) {
    val isGameStarted: Boolean
        get() = phase in setOf(
            GamePhase.REVEALING_ROLES,
            GamePhase.READY,
            GamePhase.PLAYING_ROUNDS,
            GamePhase.ADVANCING_ROUND,
            GamePhase.VOTING,
        )

    val isReadyToPlay: Boolean
        get() = phase == GamePhase.READY

    val isLoading: Boolean
        get() = phase == GamePhase.LOADING
}

enum class GamePhase {
    CONFIGURATION,
    LOADING,
    REVEALING_ROLES,
    READY,
    PLAYING_ROUNDS,
    ADVANCING_ROUND,
    VOTING,
}

enum class PlayerRole {
    CITIZEN,
    IMPOSTOR,
    UNKNOWN,
}

sealed interface GameAction {

    data class StartGame(
        val totalPlayers: Int,
        val impostors: Int,
        val rounds: Int,
        val useCustomWord: Boolean,
        val wordInput: String?,
        val category: String?, // Category filter, null means all
        val difficulty: String?, // Difficulty filter, null means all
    ) : GameAction

    data object RevealRole : GameAction
    data object HideRoleAndNext : GameAction
    data object StartRounds : GameAction
    data object FinishRound : GameAction
    data object CancelStartGame : GameAction
    data object ResetGame : GameAction
    data class SetCustomWordMode(val enabled: Boolean) : GameAction
    data class SetCustomWord(val word: String) : GameAction

    data class SetHapticsEnabled(val enabled: Boolean) : GameAction
    data class SetAnimationsEnabled(val enabled: Boolean) : GameAction
    data class SetKeepScreenOn(val enabled: Boolean) : GameAction

}

sealed interface GameEffect {
    data class ShowError(val message: String) : GameEffect
    data object NavigateToRevealRoles : GameEffect
    data object NavigateToReadyToPlay : GameEffect
    data object NavigateToRound : GameEffect
    data object NavigateToVote : GameEffect
}
