package com.github.honqout.tvlauncher3.application

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.github.honqout.tvlauncher3.coil.fetcher.ActivityIconFetcher
import com.github.honqout.tvlauncher3.coil.fetcher.AppIconFetcher
import com.github.honqout.tvlauncher3.coil.keyer.ActivityIconKeyer
import com.github.honqout.tvlauncher3.coil.keyer.AppIconKeyer

class MyApplication : Application(), SingletonImageLoader.Factory {

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(ActivityIconFetcher.Factory(this@MyApplication))
                add(ActivityIconKeyer())
                add(AppIconFetcher.Factory(this@MyApplication))
                add(AppIconKeyer())
            }
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.2)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100 * 1024 * 1024)
                    .build()
            }
            .apply {
                crossfade(true)
                logger(DebugLogger())
            }
            .build()
    }
}