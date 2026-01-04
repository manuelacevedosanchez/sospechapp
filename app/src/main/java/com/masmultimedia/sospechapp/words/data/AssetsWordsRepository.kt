package com.masmultimedia.sospechapp.words.data

import android.content.Context
import com.masmultimedia.sospechapp.words.domain.WordsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AssetsWordsRepository(
    private val context: Context,
    private val assetFileName: String = "words_seed.json",
    private val fallBackWords: List<String> = listOf("fallo", "error", "problema"),
) : WordsRepository {

    @Volatile
    private var cachedWords: List<String>? = null

    override suspend fun syncIfNeeded() {
        // No sync needed for assets
    }

    override suspend fun getRandomWord(): String = withContext(Dispatchers.IO) {
        val words = cachedWords ?: loadWordsSafely().also { cachedWords = it }
        (words.ifEmpty { fallBackWords }).random()
    }

    private fun loadWordsSafely(): List<String> {
        return runCatching {
            val json = context.assets.open(assetFileName)
                .bufferedReader()
                .use { it.readText() }

            val root = JSONObject(json)
            val wordsArray = root.getJSONArray("words")

            buildList {
                for (i in 0 until wordsArray.length()) {
                    val obj = wordsArray.getJSONObject(i)
                    val text = obj.optString("text").trim()
                    if (text.isNotBlank()) add(text)
                }
            }.distinct()
        }.getOrElse {
            emptyList()
        }
    }

}