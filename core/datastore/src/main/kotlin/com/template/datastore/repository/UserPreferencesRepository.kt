package com.template.datastore.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_TOKEN = stringPreferencesKey("user_token")
        private val USER_ID = stringPreferencesKey("user_id")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val LANGUAGE = stringPreferencesKey("language")
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    val userToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_TOKEN]
    }

    val userId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    val themeMode: Flow<String> = dataStore.data.map { preferences ->
        preferences[THEME_MODE] ?: "system"
    }

    val language: Flow<String> = dataStore.data.map { preferences ->
        preferences[LANGUAGE] ?: "en"
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun setUserToken(token: String) {
        dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    suspend fun setUserId(id: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = id
        }
    }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE] = language
        }
    }

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
