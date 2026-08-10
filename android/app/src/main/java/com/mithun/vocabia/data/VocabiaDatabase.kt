package com.mithun.vocabia.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WordEntity::class, UserWordProgressEntity::class, SessionCounterEntity::class],
    version = 1,
    exportSchema = false
)
abstract class VocabiaDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun progressDao(): ProgressDao
    abstract fun sessionCounterDao(): SessionCounterDao

    companion object {
        @Volatile private var instance: VocabiaDatabase? = null

        fun get(context: Context): VocabiaDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, VocabiaDatabase::class.java, "vocabia.db")
                    .build()
                    .also { instance = it }
            }
    }
}
