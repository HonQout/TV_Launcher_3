package com.github.honqout.tvlauncher3.room.repository

import android.content.Context
import com.github.honqout.tvlauncher3.room.dao.IconDao
import com.github.honqout.tvlauncher3.room.db.LauncherDatabase

class LauncherRepository {
    val dao: IconDao

    constructor(context: Context) {
        val db = LauncherDatabase.getDatabase(context)
        dao = db.iconDao()
    }
}