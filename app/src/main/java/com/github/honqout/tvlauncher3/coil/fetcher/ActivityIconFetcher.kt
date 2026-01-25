package com.github.honqout.tvlauncher3.coil.fetcher

import android.content.Context
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.request.Options
import com.github.honqout.tvlauncher3.coil.model.ActivityIconModel
import com.github.honqout.tvlauncher3.utils.ApplicationUtils

class ActivityIconFetcher(
    private val context: Context,
    private val data: ActivityIconModel,
    private val options: Options,
) : Fetcher {
    override suspend fun fetch(): FetchResult {
        val (_, icon) = ApplicationUtils.getActivityIconPair(
            context,
            data.packageName,
            data.activityName
        )
        return ImageFetchResult(
            image = icon.asImage(),
            isSampled = false,
            dataSource = DataSource.DISK
        )
    }

    class Factory(private val context: Context) : Fetcher.Factory<ActivityIconModel> {
        override fun create(
            data: ActivityIconModel,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher {
            return ActivityIconFetcher(
                context = context,
                data = data,
                options = options
            )
        }
    }
}