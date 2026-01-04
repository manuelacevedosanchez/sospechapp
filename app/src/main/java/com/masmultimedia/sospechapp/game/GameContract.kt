package com.masmultimedia.sospechapp.game

data class GameState(
    val totalPlayers: Int = 0,
    val impostors: Int = 0,
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
        val wordInput: String?,
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
}