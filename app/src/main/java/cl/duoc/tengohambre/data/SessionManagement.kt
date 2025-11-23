package cl.duoc.tengohambre.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session")

object SessionManager {

    private val KEY_NAME = stringPreferencesKey("user_name")
    private val KEY_LOGGED = booleanPreferencesKey("is_logged")

    suspend fun saveSession(context: Context, name: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NAME] = name
            prefs[KEY_LOGGED] = true
        }
    }

    fun getUserName(context: Context): Flow<String?> =
        context.dataStore.data.map { prefs ->
            prefs[KEY_NAME]
        }

    fun isLogged(context: Context): Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[KEY_LOGGED] ?: false
        }

    suspend fun clearSession(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
