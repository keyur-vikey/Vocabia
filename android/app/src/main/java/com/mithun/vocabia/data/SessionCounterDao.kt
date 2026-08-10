package com.mithun.vocabia.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "session_counter")
data class SessionCounterEntity(
    @PrimaryKey val id: Int = 0,
    val currentSession: Int = 0
)

@Dao
interface SessionCounterDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(counter: SessionCounterEntity)

    @Query("SELECT * FROM session_counter WHERE id = 0")
    suspend fun get(): SessionCounterEntity?

    @Query("UPDATE session_counter SET currentSession = :value WHERE id = 0")
    suspend fun set(value: Int)
}
