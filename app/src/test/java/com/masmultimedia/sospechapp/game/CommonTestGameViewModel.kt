package com.masmultimedia.sospechapp.game

enum class PlayerRole { IMPOSTOR, CITIZEN }

class TestGameViewModel {
    fun generateRoles(totalPlayers: Int, impostors: Int): List<PlayerRole> {
        val roles = MutableList(impostors) { PlayerRole.IMPOSTOR } +
                MutableList(totalPlayers - impostors) { PlayerRole.CITIZEN }
        return roles.shuffled()
    }
    fun isValidPlayers(players: Int, impostors: Int): Boolean {
        return players >= 3 && impostors in 1 until players
    }
    fun selectWord(word: String?): String {
        return if (word.isNullOrBlank()) "TestWord" else word
    }
}

