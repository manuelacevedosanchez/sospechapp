package com.masmultimedia.sospechapp.words.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordsDao {
    @Query("SELECT text FROM words ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(): String?

    @Query("SELECT text FROM words WHERE (:category IS NULL OR category = :category) AND (:difficulty IS NULL OR difficulty = :difficulty) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWordFiltered(category: String?, difficulty: String?): String?

    @Query("SELECT * FROM words WHERE (:category IS NULL OR category = :category) AND (:difficulty IS NULL OR difficulty = :difficulty)")
    suspend fun getWordsFiltered(category: String?, difficulty: String?): List<WordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<WordEntity>)

    @Query("DELETE FROM words")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getCount(): Int
}
