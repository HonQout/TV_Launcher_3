package com.github.honqout.tvlauncher3.coil.fetcher

import android.content.Context
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.request.Options
import com.github.honqout.tvlauncher3.utils.ApplicationUtils

data class AppIconModel(val packageName: String)

class AppIconFetcher(
    private val context: Context,
    private val data: AppIconModel,
    private val options: Options
) : Fetcher {
    companion object {
        const val TAG: String = "AppIconFetcher"
    }

    override suspend fun fetch(): FetchResult {
        val (_, icon) = ApplicationUtils.getApplicationIconPair(context, data.packageName)
        return ImageFetchResult(
            image = icon.asImage(),
            isSampled = false,
            dataSource = DataSource.DISK
        )
    }

    class Factory(private val context: Context) : Fetcher.Factory<AppIconModel> {
        override fun create(
            data: AppIconModel,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher {
            return AppIconFetcher(
                context = context,
                data = data,
                options = options
            )
        }
    }
}