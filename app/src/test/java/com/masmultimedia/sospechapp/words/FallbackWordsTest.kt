package com.masmultimedia.sospechapp.words

import com.google.common.truth.Truth.assertThat
import com.masmultimedia.sospechapp.words.data.fallbackWords
import org.junit.Test

class FallbackWordsTest {
    @Test
    fun `fallback is thematic balanced and unique`() {
        assertThat(fallbackWords).hasSize(60)
        assertThat(fallbackWords.groupingBy { it.category }.eachCount().values).containsExactly(10, 10, 10, 10, 10, 10)
        assertThat(fallbackWords.groupBy { it.category }.values.all { words ->
            words.map { it.difficulty }.toSet() == setOf("easy", "medium", "hard")
        }).isTrue()
        assertThat(fallbackWords.map { it.id }.toSet()).hasSize(60)
        assertThat(fallbackWords.map { it.text.lowercase() }).containsNoneOf("fallo", "error", "problema")
    }
}
