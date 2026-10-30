package com.creative.ludoland.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ludo_preferences")

class DataStoreManager(private val context: Context) {

    companion object {
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val GAMES_PLAYED = intPreferencesKey("games_played")
        val WINS_COUNT = intPreferencesKey("wins_count")
    }

    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[SOUND_ENABLED] ?: true
    }

    val gamesPlayed: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[GAMES_PLAYED] ?: 0
    }

    val winsCount: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[WINS_COUNT] ?: 0
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SOUND_ENABLED] = enabled
        }
    }

    suspend fun incrementGamesPlayed() {
        context.dataStore.edit { prefs ->
            val current = prefs[GAMES_PLAYED] ?: 0
            prefs[GAMES_PLAYED] = current + 1
        }
    }

    suspend fun incrementWins() {
        context.dataStore.edit { prefs ->
            val current = prefs[WINS_COUNT] ?: 0
            prefs[WINS_COUNT] = current + 1
        }
    }
}
