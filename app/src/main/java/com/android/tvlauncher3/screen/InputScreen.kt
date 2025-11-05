package com.android.tvlauncher3.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel

@Composable
fun InputScreen(
    viewModel: MainViewModel = viewModel()
) {
    val tag = "InputScreen"
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val topBarHeight by viewModel.topBarHeight.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {

    }
}