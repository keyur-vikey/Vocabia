package com.mithun.vocabia.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mithun.vocabia.data.WordEntity

/**
 * Reveal stages, in tap order: meaning -> article (nouns only) -> sentence1 -> sentence2 -> sentence3 -> grammar.
 * revealCount = number of taps so far (0 = only the bare word is visible).
 */
private fun stagesFor(word: WordEntity): List<String> {
    val stages = mutableListOf("meaning")
    if (word.article != null) stages.add("article")
    stages.add("sentence1")
    stages.add("sentence2")
    stages.add("sentence3")
    stages.add("grammar")
    return stages
}

fun maxRevealCount(word: WordEntity): Int = stagesFor(word).size

@Composable
fun WordCardContent(word: WordEntity, revealCount: Int) {
    val stages = stagesFor(word)
    val revealed = stages.take(revealCount).toSet()

    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
        Text(
            text = word.word,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        if ("meaning" in revealed) {
            Text(text = word.translation, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
        }
        if ("article" in revealed && word.article != null) {
            Text(text = "Article: ${word.article}", fontSize = 15.sp, modifier = Modifier.padding(top = 4.dp))
        }

        val sentences = listOf(word.sentence1, word.sentence2, word.sentence3)
        val translations = listOf(word.sentenceTranslation1, word.sentenceTranslation2, word.sentenceTranslation3)
        listOf("sentence1", "sentence2", "sentence3").forEachIndexed { i, key ->
            if (key in revealed) {
                Text(text = sentences[i], fontSize = 15.sp, modifier = Modifier.padding(top = 10.dp))
                translations[i]?.let {
                    Text(text = it, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }

        if ("grammar" in revealed) {
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            GrammarInfo(word)
        }
    }
}

@Composable
private fun GrammarInfo(word: WordEntity) {
    when (word.category) {
        "verb" -> Column {
            Text("Perfect form: ${word.verbPerfectForm ?: "-"}", fontWeight = FontWeight.SemiBold)
            Text("Auxiliary: ${word.verbAuxiliary ?: "-"}")
            if (word.verbSeparable == true) {
                Text("Separable prefix: ${word.verbSeparablePrefix ?: "-"}")
            }
            Text("Present tense:", modifier = Modifier.padding(top = 8.dp), fontWeight = FontWeight.SemiBold)
            listOf(
                "ich" to word.verbPresentIch,
                "du" to word.verbPresentDu,
                "er/sie/es" to word.verbPresentErSieEs,
                "wir" to word.verbPresentWir,
                "ihr" to word.verbPresentIhr,
                "sie/Sie" to word.verbPresentSieSie
            ).forEach { (pronoun, form) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(pronoun)
                    Text(form ?: "-")
                }
            }
        }
        "noun" -> Column {
            Text("Plural: ${word.nounPlural ?: "-"}")
            Text("Gender: ${word.nounGender ?: "-"}")
        }
        "adjective" -> Column {
            Text("Comparative: ${word.adjectiveComparative ?: "-"}")
            Text("Superlative: ${word.adjectiveSuperlative ?: "-"}")
        }
        "preposition" -> Column {
            Text("Case: ${word.prepositionCase ?: "-"}")
        }
        else -> {}
    }
}
