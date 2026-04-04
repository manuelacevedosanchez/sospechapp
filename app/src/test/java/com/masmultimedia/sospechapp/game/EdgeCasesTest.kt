package com.masmultimedia.sospechapp.game

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EdgeCasesTest {
    @Test
    fun `no permite partida con 0 jugadores`() {
        val vm = TestGameViewModel()
        assertThat(vm.isValidPlayers(0, 0)).isFalse()
    }

    @Test
    fun `no permite palabra nula o vacía`() {
        val vm = TestGameViewModel()
        val palabra = vm.selectWord(null)
        assertThat(palabra).isEqualTo("TestWord")
        val palabra2 = vm.selectWord("")
        assertThat(palabra2).isEqualTo("TestWord")
    }
}
