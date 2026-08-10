package com.mithun.vocabia.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<WordEntity>)

    @Query("SELECT COUNT(*) FROM words")
    suspend fun count(): Int

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getById(id: String): WordEntity?

    @Query("SELECT * FROM words ORDER BY frequencyRank ASC")
    suspend fun getAll(): List<WordEntity>
}
