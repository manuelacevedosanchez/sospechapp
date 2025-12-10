package com.masmultimedia.sospechapp.navigation

sealed class SospechAppDestination(val route: String) {
    data object Splash : SospechAppDestination("splash")
    data object MainMenu : SospechAppDestination("main_menu")
    // Más adelante añadiremos:
    // data object GameConfig : SospechAppDestination("game_config")
    // data object RevealRoles : SospechAppDestination("reveal_roles")
    // data object ReadyToPlay : SospechAppDestination("ready_to_play")
}