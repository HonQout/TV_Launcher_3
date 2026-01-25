package com.github.honqout.tvlauncher3.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.honqout.tvlauncher3.bean.ActivityRecord
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(context: Context) {
    companion object {
        private const val TAG: String = "SettingsRepository"
        private val FIXED_ACTIVITY_RECORD = stringPreferencesKey("fixed_activity_record")
        private const val NUM_FIXED_ACTIVITY = 5
    }

    private val dataStore = context.applicationContext.dataStore

    suspend fun saveFixedActivityRecord(list: List<ActivityRecord?>) {
        try {
            dataStore.edit { preferences ->
                val json = Gson().toJson(list)
                preferences[FIXED_ACTIVITY_RECORD] = json
            }
        } catch (e: Exception) {
            Log.e(TAG, "Cannot save fixed activity record.", e)
            throw e
        }
    }

    val fixedActivityRecordFlow: Flow<List<ActivityRecord?>> =
        dataStore.data.map { preferences ->
            val json = preferences[FIXED_ACTIVITY_RECORD]
                ?: return@map List(NUM_FIXED_ACTIVITY) { null }
            try {
                val type = object : TypeToken<List<ActivityRecord?>>() {}.type
                Gson().fromJson(json, type) ?: List(NUM_FIXED_ACTIVITY) { null }
            } catch (e: Exception) {
                Log.e(TAG, "Cannot parse fixed activity record by JSON.", e)
                List(NUM_FIXED_ACTIVITY) { null }
            }
        }.flowOn(Dispatchers.IO)
}