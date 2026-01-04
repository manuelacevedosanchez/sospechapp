package com.masmultimedia.sospechapp.words.data.remote

data class WordsResponseDto(
    val version: Int,
    val words: List<WordDto>
)
