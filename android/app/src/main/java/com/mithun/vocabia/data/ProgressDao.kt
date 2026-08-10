package com.mithun.vocabia.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProgressDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(progress: UserWordProgressEntity)

    @Update
    suspend fun update(progress: UserWordProgressEntity)

    @Query("SELECT * FROM user_word_progress WHERE wordId = :wordId")
    suspend fun getForWord(wordId: String): UserWordProgressEntity?

    @Query("SELECT * FROM user_word_progress")
    suspend fun getAll(): List<UserWordProgressEntity>

    @Query("SELECT COUNT(*) FROM user_word_progress WHERE status = 'finished'")
    suspend fun finishedCount(): Int
}
