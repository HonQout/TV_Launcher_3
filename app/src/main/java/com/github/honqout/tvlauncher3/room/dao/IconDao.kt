package com.github.honqout.tvlauncher3.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.honqout.tvlauncher3.room.entity.IconEntity

@Dao
interface IconDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg iconEntities: IconEntity)

    @Update
    suspend fun update(vararg iconEntities: IconEntity)

    @Delete
    suspend fun delete(vararg iconEntities: IconEntity)

    @Query("select * from icons")
    suspend fun getAll(): List<IconEntity>
}