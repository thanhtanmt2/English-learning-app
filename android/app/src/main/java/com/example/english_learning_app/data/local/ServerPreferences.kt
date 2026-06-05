package com.example.english_learning_app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.english_learning_app.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.serverDataStore by preferencesDataStore(name = "server_prefs")

object ServerPreferences {
    private val KEY_BASE_URL = stringPreferencesKey("base_url")

    fun baseUrlFlow(context: Context): Flow<String> =
        context.serverDataStore.data.map { prefs ->
            prefs[KEY_BASE_URL] ?: BuildConfig.DEFAULT_BASE_URL
        }

    suspend fun setBaseUrl(context: Context, url: String) {
        context.serverDataStore.edit { prefs ->
            prefs[KEY_BASE_URL] = url.trimEnd('/') + "/"
        }
    }

    suspend fun getBaseUrl(context: Context): String =
        baseUrlFlow(context).first()
}
