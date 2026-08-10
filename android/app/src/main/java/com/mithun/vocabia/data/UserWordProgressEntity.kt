package com.mithun.vocabia.data

import androidx.room.Entity
import androidx.room.PrimaryKey

const val STATUS_LEARNING = "learning"
const val STATUS_FINISHED = "finished"

@Entity(tableName = "user_word_progress")
data class UserWordProgressEntity(
    @PrimaryKey val wordId: String,
    val boxLevel: Int = 0,
    val consecutiveCorrect: Int = 0,
    val nextEligibleAt: Int = 0,
    val status: String = STATUS_LEARNING,
    val timesSeen: Int = 0
)
