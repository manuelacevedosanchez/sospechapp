package com.masmultimedia.sospechapp.words

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.masmultimedia.sospechapp.words.data.WordAssetDto
import com.masmultimedia.sospechapp.words.data.WordsAssetRoot
import com.masmultimedia.sospechapp.words.data.stableId
import org.junit.Test
import java.io.File
import java.text.Normalizer
import java.util.Locale

class WordsSeedCatalogTest {
    private val root: WordsAssetRoot by lazy {
        val file = File("src/main/assets/words_seed.json")
        assertThat(file.exists()).isTrue()
        Gson().fromJson(file.readText(), WordsAssetRoot::class.java)
    }

    @Test
    fun `real catalog has expected size and distribution`() {
        assertThat(root.words).hasSize(600)
        assertThat(root.words.groupingBy { it.category }.eachCount()).containsExactly(
            "comida", 100,
            "objetos", 100,
            "lugares", 100,
            "naturaleza", 100,
            "animales", 100,
            "personajes", 100,
        )
        root.words.groupBy { it.category to it.difficulty }.values.forEach { assertThat(it.size).isAtLeast(30) }
    }

    @Test
    fun `real catalog uses valid fields and unique stable ids`() {
        val validCategories = setOf("comida", "objetos", "lugares", "naturaleza", "animales", "personajes")
        val validDifficulties = setOf("easy", "medium", "hard")
        assertThat(root.words.all {
            !it.text.isNullOrBlank() && !it.category.isNullOrBlank() && !it.difficulty.isNullOrBlank()
        }).isTrue()
        assertThat(root.words.map { it.category }.toSet()).containsExactlyElementsIn(validCategories)
        assertThat(root.words.map { it.difficulty }.toSet()).containsExactlyElementsIn(validDifficulties)
        val ids = root.words.map {
            stableId(requireNotNull(it.category), requireNotNull(it.difficulty), requireNotNull(it.text))
        }
        assertThat(ids.toSet()).hasSize(ids.size)
    }

    @Test
    fun `real catalog has no normalized duplicate texts or technical fallbacks`() {
        val normalized = root.words.mapNotNull(WordAssetDto::text).map(::normalize)
        assertThat(normalized.toSet()).hasSize(normalized.size)
        assertThat(normalized).containsNoneOf("fallo", "error", "problema")
    }

    private fun normalize(value: String): String =
        Normalizer.normalize(value.trim().lowercase(Locale.ROOT), Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")
            .replace("\\s+".toRegex(), " ")
}
