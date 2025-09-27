package com.android.tvlauncher3.page

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tvlauncher3.R
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel
import com.android.tvlauncher3.view.button.RoundRectButton

@Composable
fun LauncherRoute(context: Context, toDestination: () -> Unit, viewModel: MainViewModel) {
    LauncherPage(context = context, toDestination = toDestination, viewModel = viewModel)
}

@Composable
fun LauncherPage(
    context: Context,
    toDestination: () -> Unit = {},
    viewModel: MainViewModel = viewModel()
) {
    val numColumns = 5
    val lazyGridState = rememberLazyGridState()
    val topBarHeight by viewModel.topBarHeight.collectAsState()

    Box(
        modifier = Modifier
            .background(color = Color.Transparent)
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .height(topBarHeight.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.title_page_home),
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(numColumns),
                modifier = Modifier,
                state = lazyGridState,
                contentPadding = PaddingValues(all = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                item {
                    RoundRectButton(
                        drawableRes = R.drawable.baseline_input_24,
                        contentDescription = stringResource(R.string.signal_source),
                        label = stringResource(R.string.signal_source),
                        onShortClickCallback = {

                        }
                    )
                }

                item {
                    RoundRectButton(
                        drawableRes = R.drawable.baseline_apps_24,
                        contentDescription = stringResource(R.string.title_page_apps),
                        label = stringResource(R.string.title_page_apps),
                        onShortClickCallback = {
                            toDestination()
                        }
                    )
                }

                item {
                    RoundRectButton(
                        drawableRes = R.drawable.baseline_settings_24,
                        contentDescription = stringResource(R.string.settings),
                        label = stringResource(R.string.settings),
                        onShortClickCallback = {

                        },
                    )
                }
            }
        }
    }
}