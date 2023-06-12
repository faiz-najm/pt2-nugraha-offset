package org.d3if3155.MoMi.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val PREFERENCES_NAME = "preferences"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_NAME
)

class SettingDataStore(prefDataStore: DataStore<Preferences>) {

    private val IS_SIGN_IN = booleanPreferencesKey("is_starter_finish")

    // val for save object UserEntity to dataStore
    private val USER_AUTH_KEY = stringPreferencesKey("user_auth")

    val isSignIn: Flow<Boolean> = prefDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { it[IS_SIGN_IN] ?: false }

    val userAuthKey: Flow<Any> = prefDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[USER_AUTH_KEY] ?: "0"
        }

    suspend fun saveFirstTime(isSignIn: Boolean, context: Context) {
        context.dataStore.edit { it[IS_SIGN_IN] = isSignIn }
    }

    suspend fun saveAuthKey(userAuthKey: String, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[USER_AUTH_KEY] = userAuthKey
        }
    }
}
