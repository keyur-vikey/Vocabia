package com.mithun.vocabia.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

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

fun categoryIcon(category: String): ImageVector = when (category) {
    "noun" -> Icons.Filled.MenuBook
    "verb" -> Icons.Filled.DirectionsRun
    "adjective" -> Icons.Filled.Palette
    "preposition" -> Icons.Filled.CompareArrows
    else -> Icons.Filled.Label
}
