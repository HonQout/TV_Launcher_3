package com.android.tvlauncher3.entity

import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppEntity(
    var packageName: String,
    var icon: Drawable,
    var label: String,
    var activityInfo: ActivityInfo
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}