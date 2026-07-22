package com.masmultimedia.sospechapp.words.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CategoryHistoryPrefs(private val context: Context) {
    private val LAST_CATEGORY_KEY = stringPreferencesKey("last_category")
    private val WORD_HISTORY_KEY = stringPreferencesKey("word_history_v2")
    private val LEGACY_RECENT_WORDS_KEY = stringPreferencesKey("recent_words")

    suspend fun getLastCategory(): String? {
        val prefs = context.wordsDataStore.data.first()
        return prefs[LAST_CATEGORY_KEY]
    }

    suspend fun setLastCategory(category: String) {
        context.wordsDataStore.edit { prefs ->
            prefs[LAST_CATEGORY_KEY] = category
        }
    }

    suspend fun getUsedWordIds(historyKey: String): Set<String> {
        val prefs = context.wordsDataStore.data.first()
        val json = prefs[WORD_HISTORY_KEY] ?: return emptySet()
        return runCatching { Json.decodeFromString<Map<String, List<String>>>(json) }
            .getOrDefault(emptyMap())[historyKey]
            .orEmpty()
            .toSet()
    }

    suspend fun addUsedWordId(historyKey: String, wordId: String) {
        context.wordsDataStore.edit { prefs ->
            val histories = prefs[WORD_HISTORY_KEY]
                ?.let { runCatching { Json.decodeFromString<Map<String, List<String>>>(it) }.getOrNull() }
                .orEmpty()
                .toMutableMap()
            histories[historyKey] = (histories[historyKey].orEmpty() + wordId).distinct()
            prefs[WORD_HISTORY_KEY] = Json.encodeToString(histories)
        }
    }

    suspend fun clearWordHistory(historyKey: String) {
        context.wordsDataStore.edit { prefs ->
            val histories = prefs[WORD_HISTORY_KEY]
                ?.let { runCatching { Json.decodeFromString<Map<String, List<String>>>(it) }.getOrNull() }
                .orEmpty()
                .toMutableMap()
            histories.remove(historyKey)
            prefs[WORD_HISTORY_KEY] = Json.encodeToString(histories)
        }
    }

    suspend fun clearHistory() {
        context.wordsDataStore.edit { prefs ->
            prefs.remove(LAST_CATEGORY_KEY)
            prefs.remove(WORD_HISTORY_KEY)
            prefs.remove(LEGACY_RECENT_WORDS_KEY)
        }
    }
}
