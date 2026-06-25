package com.github.honqout.tvlauncher3.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "icons",
    indices = [Index(value = ["package_name", "activity_name"])]
)
data class IconEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "index")
    var index: Int,
    @ColumnInfo(name = "package_name")
    var packageName: String,
    @ColumnInfo(name = "activity_name")
    var activityName: String
) {
    fun getKey(): String {
        return "$packageName:$activityName"
    }
}