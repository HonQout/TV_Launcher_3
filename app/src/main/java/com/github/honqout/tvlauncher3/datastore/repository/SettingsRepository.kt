package com.github.honqout.tvlauncher3.datastore.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(context: Context) {
    companion object {
        private const val TAG: String = "SettingsRepository"
    }

    private val dataStore = context.applicationContext.dataStore
}