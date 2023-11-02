package com.hilmisatrio.storyku.data.dsprefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AuthPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val IS_LOGIN = booleanPreferencesKey("is_login")
    private val TOKEN = stringPreferencesKey("token")
    private val NAME = stringPreferencesKey("name")

    fun getSessionLogin(): Flow<Boolean> {
        return dataStore.data.map {
            it[IS_LOGIN] ?: false
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map {
            it[TOKEN] ?: ""
        }
    }

    fun getName(): Flow<String> {
        return dataStore.data.map {
            it[NAME] ?: ""
        }
    }

    suspend fun saveDataAuth(isLoginSession: Boolean, tokenAccess: String, name: String) {
        dataStore.edit {
            it[IS_LOGIN] = isLoginSession
            it[TOKEN] = tokenAccess
            it[NAME] = name
        }
    }


    suspend fun clearData() {
        dataStore.edit {
            it.remove(IS_LOGIN)
            it.remove(TOKEN)
            it.remove(NAME)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthPreferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): AuthPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}