package com.android.tvlauncher3.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.tvlauncher3.dao.AppDao
import com.android.tvlauncher3.entity.AppEntity

@Database(version = 1, entities = [AppEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "app_database"
            )
                .build().apply { instance = this }
        }
    }
}