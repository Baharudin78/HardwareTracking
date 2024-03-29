package com.tracking.hardwaretracking.core

import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tracking.hardwaretracking.util.Constants.MY_PREF
import com.tracking.hardwaretracking.util.Constants.USER_NAME_KEY
import com.tracking.hardwaretracking.util.Constants.USER_ROLE_KEY
import com.tracking.hardwaretracking.util.Constants.USER_TOKEN

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = MY_PREF)

@Singleton
class TokenDataStore @Inject constructor(val context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val USER_TOKEN_KEY = stringPreferencesKey(USER_TOKEN)
        private val USER_ROLE = stringPreferencesKey(USER_ROLE_KEY)
        private val USER_NAME = stringPreferencesKey(USER_NAME_KEY)
    }

    suspend fun saveUserToken(token: String) {
        dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = token
        }
    }

    suspend fun saveRoleLogin(role : String){
        dataStore.edit { preference ->
            preference[USER_ROLE] = role
        }
    }

    suspend fun saveNameUser(name : String){
        dataStore.edit { preference ->
            preference[USER_NAME] = name
        }
    }

    suspend fun clearUserToken() {
        dataStore.edit { preferences ->
            preferences.remove(USER_TOKEN_KEY)
        }
    }

    val userTokenFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[USER_TOKEN_KEY] ?: ""
        }
    val userRole : Flow<String> = dataStore.data
        .map { preference ->
            preference[USER_ROLE] ?: ""
        }
    val userName : Flow<String> = dataStore.data
        .map { preference ->
            preference[USER_NAME] ?: ""
        }
}