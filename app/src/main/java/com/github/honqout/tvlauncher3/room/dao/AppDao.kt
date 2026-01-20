package com.github.honqout.tvlauncher3.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.honqout.tvlauncher3.room.entity.AppEntity

@Dao
interface AppDao {
    @Insert
    fun insertApp(app: AppEntity): Long

    @Update
    fun updateApp(app: AppEntity)

    @Query("select * from AppEntity")
    fun loadAllApps(): List<AppEntity>

    @Delete
    fun deleteApp(app: AppEntity)
}