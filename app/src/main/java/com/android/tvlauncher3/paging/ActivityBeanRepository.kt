package com.android.tvlauncher3.paging

import android.content.Context
import android.content.pm.ResolveInfo
import androidx.paging.Pager
import androidx.paging.PagingConfig

class ActivityBeanRepository(
    private val context: Context
) {
    fun getActivityBeansPagingStream(resolveInfos: List<ResolveInfo>) = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        )
    ) {
        ActivityBeanPagingSource(context, resolveInfos)
    }.flow
}