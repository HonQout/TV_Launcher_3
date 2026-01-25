package com.github.honqout.tvlauncher3.coil.keyer

import coil3.key.Keyer
import coil3.request.Options
import com.github.honqout.tvlauncher3.coil.model.AppIconModel

class AppIconKeyer : Keyer<AppIconModel> {
    override fun key(data: AppIconModel, options: Options): String {
        return data.packageName
    }
}