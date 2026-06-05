package com.example.english_learning_app.data.local

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.profileDataStore by preferencesDataStore(name = "profile_prefs")

object ProfilePreferences {
    private val KEY_AVATAR_URI = stringPreferencesKey("avatar_uri")

    fun avatarUriFlow(context: Context): Flow<Uri?> =
        context.profileDataStore.data.map { prefs ->
            prefs[KEY_AVATAR_URI]?.let { Uri.parse(it) }
        }

    suspend fun setAvatarUri(context: Context, uri: Uri) {
        context.profileDataStore.edit { prefs ->
            prefs[KEY_AVATAR_URI] = uri.toString()
        }
    }

    suspend fun clearAvatarUri(context: Context) {
        context.profileDataStore.edit { prefs ->
            prefs.remove(KEY_AVATAR_URI)
        }
    }
}
