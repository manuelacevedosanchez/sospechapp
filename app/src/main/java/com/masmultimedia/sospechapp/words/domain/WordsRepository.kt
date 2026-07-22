package com.masmultimedia.sospechapp.words.domain

data class Word(
    val id: String,
    val text: String,
    val category: String,
    val difficulty: String,
)

enum class CatalogFallbackReason {
    EMPTY_CATALOG,
    READ_ERROR,
}

sealed interface WordsResult {
    val words: List<Word>

    data class Success(override val words: List<Word>) : WordsResult

    data class Fallback(
        override val words: List<Word>,
        val reason: CatalogFallbackReason,
    ) : WordsResult

    data object Empty : WordsResult {
        override val words: List<Word> = emptyList()
    }

    data class Error(val cause: Throwable) : WordsResult {
        override val words: List<Word> = emptyList()
    }
}

interface WordsRepository {
    suspend fun getWords(category: String? = null, difficulty: String? = null): WordsResult
    suspend fun syncIfNeeded()
}
