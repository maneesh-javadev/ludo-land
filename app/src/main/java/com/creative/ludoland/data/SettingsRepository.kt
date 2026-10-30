package com.creative.ludoland.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.creative.ludoland.game.AiDifficulty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "ludo_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val SOUND_ENABLED = booleanPreferencesKey("settings_sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("settings_vibration_enabled")
        val COLOR_BLIND_MODE = booleanPreferencesKey("settings_color_blind_mode")
        val DEFAULT_AI_DIFFICULTY = stringPreferencesKey("settings_ai_difficulty")
        val PLAYER_NAME = stringPreferencesKey("settings_player_name")
    }

    val soundEnabled: Flow<Boolean> = context.settingsDataStore.data.map { prefs ->
        prefs[SOUND_ENABLED] ?: true
    }

    val vibrationEnabled: Flow<Boolean> = context.settingsDataStore.data.map { prefs ->
        prefs[VIBRATION_ENABLED] ?: true
    }

    val colorBlindMode: Flow<Boolean> = context.settingsDataStore.data.map { prefs ->
        prefs[COLOR_BLIND_MODE] ?: false
    }

    val defaultAiDifficulty: Flow<AiDifficulty> = context.settingsDataStore.data.map { prefs ->
        val name = prefs[DEFAULT_AI_DIFFICULTY] ?: AiDifficulty.MEDIUM.name
        try {
            AiDifficulty.valueOf(name)
        } catch (_: Exception) {
            AiDifficulty.MEDIUM
        }
    }

    val playerName: Flow<String> = context.settingsDataStore.data.map { prefs ->
        prefs[PLAYER_NAME] ?: "YOU"
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs -> prefs[SOUND_ENABLED] = enabled }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs -> prefs[VIBRATION_ENABLED] = enabled }
    }

    suspend fun setColorBlindMode(enabled: Boolean) {
        context.settingsDataStore.edit { prefs -> prefs[COLOR_BLIND_MODE] = enabled }
    }

    suspend fun setDefaultAiDifficulty(difficulty: AiDifficulty) {
        context.settingsDataStore.edit { prefs -> prefs[DEFAULT_AI_DIFFICULTY] = difficulty.name }
    }

    suspend fun setPlayerName(name: String) {
        context.settingsDataStore.edit { prefs -> prefs[PLAYER_NAME] = name }
    }
}
