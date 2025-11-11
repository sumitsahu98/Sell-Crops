package com.example.authapp.utils

import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

object TranslatorHelper {

    suspend fun autoTranslateText(text: String, targetLang: String = "hi"): String {
        if (text.isBlank()) return text

        // 1. Detect language
        val languageIdentifier = LanguageIdentification.getClient()
        val sourceLangCode = try {
            languageIdentifier.identifyLanguage(text).await()
        } catch (e: Exception) {
            "en" // fallback
        }

        if (sourceLangCode == "und" || sourceLangCode == targetLang) return text

        // 2. Create translation options
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.fromLanguageTag(sourceLangCode) ?: TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.fromLanguageTag(targetLang) ?: TranslateLanguage.HINDI)
            .build()

        val translator = Translation.getClient(options)

        // 3. Download model if needed
        try {
            translator.downloadModelIfNeeded().await()
        } catch (e: Exception) {
            return text // fallback to original
        }

        // 4. Translate
        return try {
            translator.translate(text).await()
        } catch (e: Exception) {
            text
        }
    }
}
