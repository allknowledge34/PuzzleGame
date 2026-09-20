package com.example.puzzlegame.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.puzzlegame.model.ColorPalette
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "block_puzzle_settings")

class SettingsRepository(context: Context) {

    private val dataStore = context.settingsDataStore

    val hapticEnabledFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_HAPTIC_ENABLED] ?: false
    }

    val easyShapesFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_EASY_SHAPES] ?: false
    }

    val paletteFlow: Flow<ColorPalette> = dataStore.data.map { prefs ->
        val name = prefs[KEY_PALETTE] ?: ColorPalette.JEWEL.name
        val migrated = if (name == "VIVID" || name == "COOL_MINIMAL") "JEWEL" else name
        try {
            ColorPalette.valueOf(migrated)
        } catch (_: IllegalArgumentException) {
            ColorPalette.JEWEL
        }
    }

    suspend fun saveHapticEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_HAPTIC_ENABLED] = enabled
        }
    }

    suspend fun saveEasyShapes(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_EASY_SHAPES] = enabled
        }
    }

    suspend fun savePalette(palette: ColorPalette) {
        dataStore.edit { prefs ->
            prefs[KEY_PALETTE] = palette.name
        }
    }

    companion object {
        private val KEY_HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        private val KEY_EASY_SHAPES = booleanPreferencesKey("easy_shapes")
        private val KEY_PALETTE = stringPreferencesKey("color_palette")
    }
}