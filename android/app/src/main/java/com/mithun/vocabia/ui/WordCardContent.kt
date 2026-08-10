package com.mithun.vocabia.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val accent = categoryColor(word.category)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // header section: category chip + word
        CategoryChip(word.category, accent)
        Text(
            text = word.word,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 10.dp)
        )
        if ("article" in revealed && word.article != null) {
            Text(
                text = "Article: ${word.article}",
                fontSize = 15.sp,
                color = accent,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        if ("meaning" in revealed) {
            Section(title = "Meaning", accent = accent) {
                Text(text = word.translation, fontSize = 18.sp)
            }
        }

        val sentences = listOf(word.sentence1, word.sentence2, word.sentence3)
        val translations = listOf(word.sentenceTranslation1, word.sentenceTranslation2, word.sentenceTranslation3)
        val revealedSentenceCount = listOf("sentence1", "sentence2", "sentence3").count { it in revealed }
        if (revealedSentenceCount > 0) {
            Section(title = "Examples", accent = accent) {
                Column {
                    for (i in 0 until revealedSentenceCount) {
                        if (i > 0) androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 6.dp))
                        Text(text = "${i + 1}. ${sentences[i]}", fontSize = 15.sp)
                        translations[i]?.let {
                            Text(
                                text = it,
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        if ("grammar" in revealed) {
            Section(title = "Grammar", accent = accent) {
                GrammarInfo(word)
            }
        }
    }
}

@Composable
private fun CategoryChip(category: String, accent: Color) {
    Text(
        text = categoryLabel(category).uppercase(),
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(accent, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

@Composable
private fun Section(title: String, accent: Color, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .background(accent.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = accent,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        content()
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
