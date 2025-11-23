package com.dev.jcctech.agroguardian.data.remote.provider.token

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class AndroidTokenProvider(
    private val dataStore: DataStore<Preferences>
): TokenProvider {

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    override suspend fun getAccessToken(): String? {
        val prefs = dataStore.data.first()
        return prefs[ACCESS_TOKEN]
    }

    override suspend fun getRefreshToken(): String? {
        val prefs = dataStore.data.first()
        return prefs[REFRESH_TOKEN]
    }

    override suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
        }
    }

    override suspend fun saveRefreshToken(token: String) {
        dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN] = token
        }
    }

    override suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}