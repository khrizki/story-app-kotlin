package com.dicoding.picodiploma.loginwithanimation.data.user

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class SessionManager private constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val KEY_EMAIL = stringPreferencesKey("user_email")
        private val KEY_TOKEN = stringPreferencesKey("user_token")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(dataStore: DataStore<Preferences>): SessionManager {
            return INSTANCE ?: synchronized(this) {
                val newInstance = SessionManager(dataStore)
                INSTANCE = newInstance
                newInstance
            }
        }
    }

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[KEY_EMAIL] = user.email
            preferences[KEY_TOKEN] = user.token
            preferences[KEY_IS_LOGGED_IN] = true
        }

        Log.d("SessionManager", "Session stored successfully")
    }

    fun retrieveSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            val email = preferences[KEY_EMAIL] ?: ""
            val token = preferences[KEY_TOKEN] ?: ""
            val isLoggedIn = preferences[KEY_IS_LOGGED_IN] ?: false

            UserModel(email, token, isLoggedIn)
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
        Log.d("SessionManager", "Session cleared successfully")
    }
}
