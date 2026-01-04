package com.masmultimedia.sospechapp.words.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordsDao {
    @Query("SELECT text FROM words ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<WordEntity>)

    @Query("DELETE FROM words")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getCount(): Int
}