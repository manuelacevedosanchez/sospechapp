package com.masmultimedia.sospechapp.words.data.remote

interface WordsApi {
    // @GET("words.json")
    suspend fun getWords(): WordsResponseDto
}