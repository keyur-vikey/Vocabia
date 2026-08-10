package com.mithun.vocabia.ui

import androidx.compose.ui.graphics.Color

fun categoryColor(category: String): Color = when (category) {
    "noun" -> Color(0xFF3B82F6)
    "verb" -> Color(0xFFF97316)
    "adjective" -> Color(0xFF22C55E)
    "preposition" -> Color(0xFFA855F7)
    else -> Color(0xFF6B7280)
}

fun categoryLabel(category: String): String = when (category) {
    "noun" -> "Noun"
    "verb" -> "Verb"
    "adjective" -> "Adjective"
    "preposition" -> "Preposition"
    "adverb" -> "Adverb"
    else -> "Other"
}
