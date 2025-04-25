package com.android.tvlauncher3.page

import android.content.Context
import android.content.pm.ResolveInfo
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tvlauncher3.R
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel
import com.android.tvlauncher3.utils.IntentUtils
import com.android.tvlauncher3.view.button.RoundedRectButton
import com.android.tvlauncher3.view.dialog.AppActionDialog

@Composable
fun ApplicationsRoute(context: Context, toDestination: () -> Unit, viewModel: MainViewModel) {
    AllAppsPage(context = context, toDestination = toDestination, viewModel = viewModel)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllAppsPage(
    context: Context,
    toDestination: () -> Unit = {},
    viewModel: MainViewModel = viewModel()
) {
    val numColumns = 5
    val lazyGridState = rememberLazyGridState()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val topBarHeight by viewModel.topBarHeight.collectAsState()
    val showAppActionDialog by viewModel.showAppActionDialog.collectAsState()
    val resolveInfo: ResolveInfo? by viewModel.selectedResolveInfo.collectAsState()

    LaunchedEffect(Unit) {

    }

    Box(
        modifier = Modifier
            .background(color = Color.Transparent)
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .height(topBarHeight.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.title_page_apps),
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = viewModel.activityBeanList.size.toString(),
                    modifier = Modifier.padding(start = 10.dp),
                    color = Color.White,
                    fontSize = 30.sp,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(numColumns),
                modifier = Modifier
                    .focusGroup(),
                state = lazyGridState,
                contentPadding = PaddingValues(all = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                itemsIndexed(
                    viewModel.activityBeanList
                ) { index, item ->
                    RoundedRectButton(
                        modifier = Modifier,
                        icon = item.getIcon(context),
                        iconType = item.iconType,
                        contentDescription = item.label,
                        label = item.label,
                        onShortClickCallback = {
                            viewModel.setSelectedIndex(index)
                            var packageName = item.packageName
                            var result: Boolean =
                                IntentUtils.launchApp(context, packageName.toString(), true)
                            if (!result) {
                                Toast.makeText(
                                    context,
                                    ContextCompat.getString(
                                        context,
                                        R.string.hint_cannot_launch_app
                                    ),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        },
                        onLongClickCallback = {
                            viewModel.setSelectedIndex(index)
                            viewModel.setSelectedResolveInfo(item.resolveInfo)
                            viewModel.setShowAppActionDialog(true)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    if (showAppActionDialog && resolveInfo != null) {
        AppActionDialog(
            context = context,
            resolveInfo = resolveInfo!!,
            onDismissRequest = {
                viewModel.setShowAppActionDialog(false)
            },
        )
    }
}