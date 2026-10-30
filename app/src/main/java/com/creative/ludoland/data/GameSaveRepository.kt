package com.creative.ludoland.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.creative.ludoland.game.GameState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.gameSaveDataStore: DataStore<Preferences> by preferencesDataStore(name = "ludo_game_save")

class GameSaveRepository(private val context: Context) {

    companion object {
        val SAVED_GAME_KEY = stringPreferencesKey("saved_game_state_json")
    }

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    val savedGameState: Flow<GameState?> = context.gameSaveDataStore.data.map { prefs ->
        val rawJson = prefs[SAVED_GAME_KEY] ?: return@map null
        try {
            json.decodeFromString<GameState>(rawJson)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun saveGameState(state: GameState) {
        try {
            val serialized = json.encodeToString(state)
            context.gameSaveDataStore.edit { prefs ->
                prefs[SAVED_GAME_KEY] = serialized
            }
        } catch (_: Exception) {}
    }

    suspend fun clearSavedGame() {
        context.gameSaveDataStore.edit { prefs ->
            prefs.remove(SAVED_GAME_KEY)
        }
    }
}
