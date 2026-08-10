package com.mithun.vocabia.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object SeedLoader {

    suspend fun seedIfEmpty(context: Context, db: VocabiaDatabase, assetPath: String = "vocab/de_en_pilot.json") {
        if (db.wordDao().count() > 0) return
        val json = context.assets.open(assetPath).bufferedReader(Charsets.UTF_8).use { it.readText() }
        val array = JSONArray(json)
        val words = (0 until array.length()).map { parseWord(array.getJSONObject(it)) }
        db.wordDao().insertAll(words)
    }

    private fun parseWord(o: JSONObject): WordEntity {
        val sentences = o.getJSONArray("sentences")
        val sentenceTranslations = o.optJSONArray("sentence_translations")
        val verbInfo = o.optJSONObject("verb_info")
        val presentTense = verbInfo?.optJSONObject("present_tense")
        val nounInfo = o.optJSONObject("noun_info")
        val adjectiveInfo = o.optJSONObject("adjective_info")
        val prepositionInfo = o.optJSONObject("preposition_info")

        return WordEntity(
            id = o.getString("id"),
            language = o.getString("language"),
            baseLanguage = o.optString("base_language", "en"),
            word = o.getString("word"),
            article = o.optStringOrNull("article"),
            translation = o.getString("translation"),
            category = o.getString("category"),
            frequencyRank = o.getInt("frequency_rank"),
            cefrLevel = o.optStringOrNull("cefr_level"),
            sentence1 = sentences.getString(0),
            sentence2 = sentences.getString(1),
            sentence3 = sentences.getString(2),
            sentenceTranslation1 = sentenceTranslations?.optString(0),
            sentenceTranslation2 = sentenceTranslations?.optString(1),
            sentenceTranslation3 = sentenceTranslations?.optString(2),
            verbPerfectForm = verbInfo?.optString("perfect_form"),
            verbAuxiliary = verbInfo?.optString("auxiliary"),
            verbSeparable = verbInfo?.let { it.optBoolean("separable") },
            verbSeparablePrefix = verbInfo?.optStringOrNull("separable_prefix"),
            verbPresentIch = presentTense?.optString("ich"),
            verbPresentDu = presentTense?.optString("du"),
            verbPresentErSieEs = presentTense?.optString("er_sie_es"),
            verbPresentWir = presentTense?.optString("wir"),
            verbPresentIhr = presentTense?.optString("ihr"),
            verbPresentSieSie = presentTense?.optString("sie_Sie"),
            nounPlural = nounInfo?.optString("plural"),
            nounGender = nounInfo?.optString("gender"),
            adjectiveComparative = adjectiveInfo?.optString("comparative"),
            adjectiveSuperlative = adjectiveInfo?.optString("superlative"),
            prepositionCase = prepositionInfo?.optString("case")
        )
    }

    private fun JSONObject.optStringOrNull(key: String): String? =
        if (isNull(key) || !has(key)) null else optString(key)
}
