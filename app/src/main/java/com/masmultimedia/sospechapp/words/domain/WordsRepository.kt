package com.masmultimedia.sospechapp.words.domain

interface WordsRepository {
    suspend fun getRandomWord(): String
    suspend fun syncIfNeeded()
}