package com.mithun.vocabia.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey val id: String,
    val language: String,
    val baseLanguage: String,
    val word: String,
    val article: String?,
    val translation: String,
    val category: String,
    val frequencyRank: Int,
    val cefrLevel: String?,
    val sentence1: String,
    val sentence2: String,
    val sentence3: String,
    val sentenceTranslation1: String?,
    val sentenceTranslation2: String?,
    val sentenceTranslation3: String?,

    // verb_info
    val verbPerfectForm: String? = null,
    val verbAuxiliary: String? = null,
    val verbSeparable: Boolean? = null,
    val verbSeparablePrefix: String? = null,
    val verbPresentIch: String? = null,
    val verbPresentDu: String? = null,
    val verbPresentErSieEs: String? = null,
    val verbPresentWir: String? = null,
    val verbPresentIhr: String? = null,
    val verbPresentSieSie: String? = null,

    // noun_info
    val nounPlural: String? = null,
    val nounGender: String? = null,

    // adjective_info
    val adjectiveComparative: String? = null,
    val adjectiveSuperlative: String? = null,

    // preposition_info
    val prepositionCase: String? = null
)
