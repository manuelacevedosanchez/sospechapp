package com.masmultimedia.sospechapp.words.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.masmultimedia.sospechapp.words.domain.WordsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AssetsWordsRepository(
    private val context: Context,
    private val assetFileName: String = "words_seed.json",
    private val fallBackWords: List<String> = listOf("fallo", "error", "problema"),
) : WordsRepository {

    data class WordAsset(
        @SerializedName("text") val text: String,
        @SerializedName("category") val category: String?,
        @SerializedName("difficulty") val difficulty: String?
    )

    data class WordsRoot(
        @SerializedName("words") val words: List<WordAsset>
    )

    @Volatile
    private var cachedWords: List<WordAsset>? = null

    override suspend fun syncIfNeeded() {
        // No sync needed for assets
    }

    override suspend fun getRandomWord(category: String?, difficulty: String?): String = withContext(Dispatchers.IO) {
        val words = cachedWords ?: loadWordsSafely().also { cachedWords = it }
        val cat = category?.trim()?.lowercase()
        val diff = difficulty?.trim()?.lowercase()
        val filtered = words.filter {
            (cat == null || it.category?.trim()?.lowercase() == cat) &&
            (diff == null || it.difficulty?.trim()?.lowercase() == diff)
        }
        (filtered.ifEmpty { words }.ifEmpty { fallBackWords.map { w -> WordAsset(w, null, null) } }).random().text
    }

    private fun loadWordsSafely(): List<WordAsset> {
        return runCatching {
            val json = context.assets.open(assetFileName)
                .bufferedReader()
                .use { it.readText() }
            println("[DEBUG] JSON leído: $json")
            val root = Gson().fromJson(json, WordsRoot::class.java)
            root.words.mapNotNull { word ->
                val text = word.text.trim()
                val category = word.category?.trim().takeUnless { it.isNullOrBlank() }
                val difficulty = word.difficulty?.trim().takeUnless { it.isNullOrBlank() }
                if (text.isNotBlank()) WordAsset(text, category, difficulty) else null
            }.also { println("[DEBUG] Palabras cargadas: ${it.size}") }
        }.getOrElse {
            println("[DEBUG] Excepción al leer palabras: ${it}")
            emptyList()
        }
    }

}