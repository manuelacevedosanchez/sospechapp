package com.masmultimedia.sospechapp.words.domain

interface WordsRepository {
    suspend fun getRandomWord(category: String? = null, difficulty: String? = null): String
    suspend fun syncIfNeeded()
}