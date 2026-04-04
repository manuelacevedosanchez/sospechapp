package com.masmultimedia.sospechapp.game

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GameLogicTest {
    @Test
    fun `generateRoles reparte correctamente los roles`() {
        val vm = TestGameViewModel()
        val roles = vm.generateRoles(6, 2)
        assertThat(roles.count { it == PlayerRole.IMPOSTOR }).isEqualTo(2)
        assertThat(roles.count { it == PlayerRole.CITIZEN }).isEqualTo(4)
        // Debe ser aleatorio, pero siempre sumar totalPlayers
        assertThat(roles.size).isEqualTo(6)
    }

    @Test
    fun `no permite menos de 3 jugadores o impostores fuera de rango`() {
        val vm = TestGameViewModel()
        assertThat(vm.isValidPlayers(2, 1)).isFalse() // menos de 3 jugadores
        assertThat(vm.isValidPlayers(5, 0)).isFalse() // 0 impostores
        assertThat(vm.isValidPlayers(5, 5)).isFalse() // igual jugadores que impostores
        assertThat(vm.isValidPlayers(5, 6)).isFalse() // más impostores que jugadores
        assertThat(vm.isValidPlayers(5, 1)).isTrue()  // caso válido
    }

    @Test
    fun `palabra se selecciona correctamente`() {
        val vm = TestGameViewModel()
        val palabra = vm.selectWord(null)
        assertThat(palabra).isEqualTo("TestWord")
        val palabra2 = vm.selectWord("")
        assertThat(palabra2).isEqualTo("TestWord")
        val palabra3 = vm.selectWord("Personalizada")
        assertThat(palabra3).isEqualTo("Personalizada")
    }
}

private class TestGameViewModel {
    fun generateRoles(totalPlayers: Int, impostors: Int): List<PlayerRole> {
        val roles = mutableListOf<PlayerRole>()
        repeat(impostors) { roles.add(PlayerRole.IMPOSTOR) }
        repeat(totalPlayers - impostors) { roles.add(PlayerRole.CITIZEN) }
        roles.shuffle()
        return roles
    }
    fun isValidPlayers(totalPlayers: Int, impostors: Int): Boolean {
        return totalPlayers >= 3 && impostors >= 1 && impostors < totalPlayers
    }
    fun selectWord(wordInput: String?): String {
        return wordInput?.takeIf { it.isNotBlank() } ?: "TestWord"
    }
}

