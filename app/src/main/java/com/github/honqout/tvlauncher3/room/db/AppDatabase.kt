package com.github.honqout.tvlauncher3.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.honqout.tvlauncher3.room.dao.AppDao
import com.github.honqout.tvlauncher3.room.entity.AppEntity

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