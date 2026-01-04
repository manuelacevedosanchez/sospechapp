package com.masmultimedia.sospechapp.words.data.remote

data class WordDto(
    val text: String,
    val category: String? = null,
    val difficulty: String? = null,
)
