package com.masmultimedia.sospechapp.words.data.local

import androidx.room.*
import androidx.room.Entity

@Entity(
    tableName = "words",
    indices = [Index(value = ["text"], unique = true)]
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val category: String? = null,
    val difficulty: String? = null
)