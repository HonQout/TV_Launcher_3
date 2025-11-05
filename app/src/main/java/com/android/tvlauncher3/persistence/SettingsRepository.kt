package com.android.tvlauncher3.persistence

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.android.tvlauncher3.bean.ActivityRecord
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(context: Context) {
    companion object {
        private const val TAG: String = "SettingsRepository"
        private val FIXED_ACTIVITY_RECORD = stringPreferencesKey("fixed_activity_record")
    }

    private val dataStore = context.applicationContext.dataStore

    suspend fun saveFixedActivityRecord(list: List<ActivityRecord?>) {
        try {
            dataStore.edit { preferences ->
                val json = Gson().toJson(list)
                preferences[FIXED_ACTIVITY_RECORD] = json
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save fixed activity bean.", e)
            throw e
        }

    }

    val fixedActivityRecordFlow: Flow<List<ActivityRecord?>> =
        dataStore.data.map { preferences ->
            val json = preferences[FIXED_ACTIVITY_RECORD] ?: return@map List(5) { null }
            try {
                val type = object : TypeToken<List<ActivityRecord?>>() {}.type
                Gson().fromJson<List<ActivityRecord?>>(json, type) ?: List(5) { null }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse fixed activity bean by JSON.", e)
                List(5) { null }
            }
        }
}