package com.masmultimedia.sospechapp.game

data class GameState(
    val totalPlayers: Int = 0,
    val impostors: Int = 0,
    val rounds: Int = 1,
    val currentRound: Int = 1,
    val wordInput: String = "",
    val currentWord: String? = null,
    val roles: List<PlayerRole> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val isRoleVisible: Boolean = false,
    val isGameStarted: Boolean = false,
    val isReadyToPlay: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val settings: AppSettings = AppSettings(),
)

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
        val wordInput: String?,
        val category: String?, // Category filter, null means all
        val difficulty: String?, // Difficulty filter, null means all
    ) : GameAction

    data object RevealRole : GameAction
    data object HideRoleAndNext : GameAction
    data object ResetGame : GameAction

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