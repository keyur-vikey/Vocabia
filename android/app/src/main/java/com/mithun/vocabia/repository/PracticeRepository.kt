package com.mithun.vocabia.repository

import android.content.Context
import com.mithun.vocabia.data.STATUS_FINISHED
import com.mithun.vocabia.data.STATUS_LEARNING
import com.mithun.vocabia.data.SeedLoader
import com.mithun.vocabia.data.SessionCounterEntity
import com.mithun.vocabia.data.UserWordProgressEntity
import com.mithun.vocabia.data.VocabiaDatabase
import com.mithun.vocabia.data.WordEntity
import kotlin.random.Random

enum class SwipeDirection { LEFT, RIGHT, DOWN }

data class PracticeCard(val word: WordEntity, val progress: UserWordProgressEntity)

private const val SESSION_TARGET_SIZE = 12
private const val NEW_WORD_CAP = 5
private const val DUE_SHARE = 0.7

class PracticeRepository(context: Context) {
    private val db = VocabiaDatabase.get(context)
    private val appContext = context.applicationContext

    suspend fun ensureSeeded() {
        SeedLoader.seedIfEmpty(appContext, db)
    }

    private fun delayForBox(boxLevel: Int): Int = when (boxLevel) {
        1 -> 2
        2 -> 3
        3 -> 5
        4 -> 8
        else -> 12
    }

    suspend fun buildSession(): List<PracticeCard> {
        val allWords = db.wordDao().getAll()
        val progressByWord = db.progressDao().getAll().associateBy { it.wordId }
        val currentSession = db.sessionCounterDao().get()?.currentSession ?: 0

        val duePool = allWords.mapNotNull { word ->
            val progress = progressByWord[word.id]
            if (progress != null && progress.status == STATUS_LEARNING && progress.nextEligibleAt <= currentSession) {
                word to progress
            } else null
        }.sortedBy { it.second.nextEligibleAt }

        val newPool = allWords
            .filter { progressByWord[it.id] == null }
            .sortedBy { it.frequencyRank }

        val dueSlots = (SESSION_TARGET_SIZE * DUE_SHARE).toInt()
        val fromDue = duePool.take(dueSlots)
        val remainingSlots = SESSION_TARGET_SIZE - fromDue.size
        val fromNew = newPool.take(minOf(remainingSlots, NEW_WORD_CAP))

        val selected = (fromDue + fromNew.map { it to defaultProgress(it.id) }).toMutableList()

        // if still short (e.g. due pool empty early on), top up from new pool beyond the cap
        if (selected.size < SESSION_TARGET_SIZE) {
            val already = selected.map { it.first.id }.toSet()
            val topUp = newPool.filter { it.id !in already }
                .take(SESSION_TARGET_SIZE - selected.size)
                .map { it to defaultProgress(it.id) }
            selected.addAll(topUp)
        }

        return selected.shuffled(Random(System.nanoTime())).map { PracticeCard(it.first, it.second) }
    }

    private fun defaultProgress(wordId: String) = UserWordProgressEntity(wordId = wordId)

    suspend fun recordSwipe(word: WordEntity, direction: SwipeDirection) {
        val currentSession = db.sessionCounterDao().get()?.currentSession ?: 0
        val existing = db.progressDao().getForWord(word.id) ?: UserWordProgressEntity(wordId = word.id)
        val timesSeen = existing.timesSeen + 1

        val updated = when (direction) {
            SwipeDirection.LEFT -> existing.copy(
                consecutiveCorrect = 0,
                boxLevel = maxOf(0, existing.boxLevel - 1),
                nextEligibleAt = currentSession + 1,
                status = STATUS_LEARNING,
                timesSeen = timesSeen
            )
            SwipeDirection.RIGHT -> {
                val newConsecutive = existing.consecutiveCorrect + 1
                val newBox = minOf(5, existing.boxLevel + 1)
                existing.copy(
                    consecutiveCorrect = newConsecutive,
                    boxLevel = newBox,
                    nextEligibleAt = currentSession + delayForBox(newBox),
                    status = if (newConsecutive >= 3) STATUS_FINISHED else STATUS_LEARNING,
                    timesSeen = timesSeen
                )
            }
            SwipeDirection.DOWN -> existing.copy(
                status = STATUS_FINISHED,
                timesSeen = timesSeen
            )
        }

        db.progressDao().insertIfAbsent(UserWordProgressEntity(wordId = word.id))
        db.progressDao().update(updated)
    }

    suspend fun advanceSession() {
        db.sessionCounterDao().insertIfAbsent(SessionCounterEntity())
        val current = db.sessionCounterDao().get()?.currentSession ?: 0
        db.sessionCounterDao().set(current + 1)
    }

    suspend fun stats(): Pair<Int, Int> {
        val total = db.wordDao().count()
        val finished = db.progressDao().finishedCount()
        return finished to total
    }
}
