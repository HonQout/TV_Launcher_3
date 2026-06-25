package com.github.honqout.tvlauncher3.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.honqout.tvlauncher3.room.dao.IconDao
import com.github.honqout.tvlauncher3.room.entity.IconEntity

@Database(
    entities = [IconEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LauncherDatabase : RoomDatabase() {
    abstract fun iconDao(): IconDao

    companion object {
        private var instance: LauncherDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): LauncherDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                LauncherDatabase::class.java,
                "app_database"
            )
                .build()
                .apply { instance = this }
        }
    }
}