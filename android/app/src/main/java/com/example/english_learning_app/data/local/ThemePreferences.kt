package com.example.english_learning_app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(name = "theme_prefs")

object ThemePreferences {
    private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")

    fun darkModeFlow(context: Context): Flow<Boolean> =
        context.themeDataStore.data.map { prefs -> prefs[KEY_DARK_MODE] ?: false }

    suspend fun setDarkMode(context: Context, enabled: Boolean) {
        context.themeDataStore.edit { prefs -> prefs[KEY_DARK_MODE] = enabled }
    }
}
