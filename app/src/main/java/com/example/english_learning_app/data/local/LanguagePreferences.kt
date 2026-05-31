package com.example.english_learning_app.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val LANGUAGE_PREFS_NAME = "language_prefs"
private val Context.languageDataStore by preferencesDataStore(name = LANGUAGE_PREFS_NAME)

object LanguagePreferences {
    const val DEFAULT_LANGUAGE = "vi"
    private val KEY_LANGUAGE = stringPreferencesKey("language")

    fun languageFlow(context: Context): Flow<String> {
        return context.languageDataStore.data.map { prefs ->
            prefs[KEY_LANGUAGE] ?: DEFAULT_LANGUAGE
        }
    }

    suspend fun setLanguage(context: Context, languageTag: String) {
        context.languageDataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = languageTag
        }
    }
}
