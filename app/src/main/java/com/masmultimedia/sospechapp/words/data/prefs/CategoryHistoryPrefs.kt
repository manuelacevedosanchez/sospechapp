package com.masmultimedia.sospechapp.words.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.datastore by preferencesDataStore(name = "words_prefs")

class CategoryHistoryPrefs(private val context: Context) {
    private val LAST_CATEGORY_KEY = stringPreferencesKey("last_category")
    private val RECENT_WORDS_KEY = stringPreferencesKey("recent_words")
    private val RECENT_WORDS_LIMIT = 10

    suspend fun getLastCategory(): String? {
        val prefs = context.datastore.data.first()
        return prefs[LAST_CATEGORY_KEY]
    }

    suspend fun setLastCategory(category: String) {
        context.datastore.edit { prefs ->
            prefs[LAST_CATEGORY_KEY] = category
        }
    }

    suspend fun getRecentWords(): List<String> {
        val prefs = context.datastore.data.first()
        val json = prefs[RECENT_WORDS_KEY] ?: return emptyList()
        return runCatching { Json.decodeFromString<List<String>>(json) }.getOrDefault(emptyList())
    }

    suspend fun addRecentWord(word: String) {
        val current = getRecentWords().toMutableList()
        current.add(word)
        while (current.size > RECENT_WORDS_LIMIT) current.removeAt(0)
        val json = Json.encodeToString(current)
        context.datastore.edit { prefs ->
            prefs[RECENT_WORDS_KEY] = json
        }
    }

    suspend fun clearHistory() {
        context.datastore.edit { prefs ->
            prefs.remove(LAST_CATEGORY_KEY)
            prefs.remove(RECENT_WORDS_KEY)
        }
    }
}
