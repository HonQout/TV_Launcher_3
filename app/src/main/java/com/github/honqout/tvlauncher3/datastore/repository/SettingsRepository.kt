package com.github.honqout.tvlauncher3.datastore.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.honqout.tvlauncher3.data.model.settings.SavedSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    companion object {
        private const val TAG: String = "SettingsRepository"

        object SettingsPrefix {
            const val SWITCH = "switch_"
            const val SINGLE_CHOICE = "single_choice_"
            const val SLIDE = "slide_"
            const val INPUT = "input_"
        }
    }

    private val dataStore = context.settingsDataStore

    suspend fun putBoolean(key: String, value: Boolean) {
        dataStore.edit { prefs ->
            prefs[booleanPreferencesKey(key)] = value
        }
    }

    suspend fun putInt(key: String, value: Int) {
        dataStore.edit { prefs ->
            prefs[intPreferencesKey(key)] = value
        }
    }

    suspend fun putLong(key: String, value: Long) {
        dataStore.edit { prefs ->
            prefs[longPreferencesKey(key)] = value
        }
    }

    suspend fun putFloat(key: String, value: Float) {
        dataStore.edit { prefs ->
            prefs[floatPreferencesKey(key)] = value
        }
    }

    suspend fun putString(key: String, value: String) {
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey(key)] = value
        }
    }

    suspend fun putStringSet(key: String, value: Set<String>) {
        dataStore.edit { prefs ->
            prefs[stringSetPreferencesKey(key)] = value
        }
    }

    suspend fun putSwitch(key: String, value: Boolean) {
        val keyWithPrefix = SettingsPrefix.SWITCH + key
        putBoolean(keyWithPrefix, value)
    }

    suspend fun putSingleChoice(key: String, value: Int) {
        val keyWithPrefix = SettingsPrefix.SINGLE_CHOICE + key
        putInt(keyWithPrefix, value)
    }

    suspend fun putSlide(key: String, value: Float) {
        val keyWithPrefix = SettingsPrefix.SLIDE + key
        putFloat(keyWithPrefix, value)
    }

    suspend fun putInput(key: String, value: String) {
        val keyWithPrefix = SettingsPrefix.INPUT + key
        putString(keyWithPrefix, value)
    }

    fun loadAllSettings(): Flow<SavedSettings> = dataStore.data.map { prefs ->
        SavedSettings(
            switchValues = prefs.asMap()
                .filterKeys { it.name.startsWith(SettingsPrefix.SWITCH) }
                .mapKeys { (k, _) -> k.name.removePrefix(SettingsPrefix.SWITCH) }
                .mapValues { (_, v) -> v as Boolean },
            singleChoiceValues = prefs.asMap()
                .filterKeys { it.name.startsWith(SettingsPrefix.SINGLE_CHOICE) }
                .mapKeys { (k, _) -> k.name.removePrefix(SettingsPrefix.SINGLE_CHOICE) }
                .mapValues { (_, v) -> v as Int },
            slideValues = prefs.asMap()
                .filterKeys { it.name.startsWith(SettingsPrefix.SLIDE) }
                .mapKeys { (k, _) -> k.name.removePrefix(SettingsPrefix.SLIDE) }
                .mapValues { (_, v) -> v as Float },
            inputValues = prefs.asMap()
                .filterKeys { it.name.startsWith(SettingsPrefix.INPUT) }
                .mapKeys { (k, _) -> k.name.removePrefix(SettingsPrefix.INPUT) }
                .mapValues { (_, v) -> v as String }
        )
    }
}