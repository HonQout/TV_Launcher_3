package com.github.honqout.tvlauncher3.coil.keyer

import coil3.key.Keyer
import coil3.request.Options
import com.github.honqout.tvlauncher3.coil.model.ActivityIconModel

class ActivityIconKeyer : Keyer<ActivityIconModel> {
    override fun key(data: ActivityIconModel, options: Options): String {
        return data.packageName + ":" + data.activityName
    }
}