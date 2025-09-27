package com.android.tvlauncher3.paging

import android.content.Context
import android.content.pm.ResolveInfo
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.tvlauncher3.bean.ActivityBean

class ActivityBeanPagingSource(
    private val context: Context,
    private val resolveInfos: List<ResolveInfo>
) : PagingSource<Int, ActivityBean>() {
    override fun getRefreshKey(state: PagingState<Int, ActivityBean>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ActivityBean> {
        return try {
            val position = params.key ?: 0
            val from = position * params.loadSize
            val to = (position + 1) * params.loadSize

            val subList = if (to <= resolveInfos.size) {
                resolveInfos.subList(from, to)
            } else {
                resolveInfos.subList(from, resolveInfos.size)
            }

            val activityBeans = subList.map { resolveInfo ->
                ActivityBean(context, resolveInfo)
            }

            LoadResult.Page(
                data = activityBeans,
                prevKey = if (position == 0) null else position - 1,
                nextKey = if (activityBeans.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}