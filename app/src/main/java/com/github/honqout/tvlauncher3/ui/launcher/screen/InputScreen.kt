package com.github.honqout.tvlauncher3.ui.launcher.screen

import android.content.Context
import android.content.Intent
import android.media.tv.TvContract
import android.media.tv.TvInputInfo
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.databinding.TvViewLayoutBinding
import com.github.honqout.tvlauncher3.ui.input.activity.InputActivity
import com.github.honqout.tvlauncher3.ui.launcher.components.button.TvInputButton
import com.github.honqout.tvlauncher3.ui.launcher.viewmodel.InputViewModel
import com.github.honqout.tvlauncher3.ui.theme.OnWallpaperContainer
import com.github.honqout.tvlauncher3.ui.theme.PADDING_LIST_CONTENT_EDGE
import com.github.honqout.tvlauncher3.ui.theme.PADDING_SCREEN_EDGE
import com.github.honqout.tvlauncher3.ui.theme.PADDING_TOP_BAR
import com.github.honqout.tvlauncher3.ui.theme.SPACE_LIST_CONTENT_VERTICAL

@Composable
fun InputScreen(
    viewModel: InputViewModel = viewModel()
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val topBarHeight by viewModel.topBarHeight.collectAsState()
    val tvInputList by viewModel.tvInputList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
            .padding(PADDING_SCREEN_EDGE)
    ) {
        Spacer(modifier = Modifier.height(topBarHeight.dp + PADDING_TOP_BAR))

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Transparent),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            val isFocused by interactionSource.collectIsFocusedAsState()
            val isHovered by interactionSource.collectIsHoveredAsState()

            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.2f)
                    .background(
                        color = OnWallpaperContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .focusRestorer(),
                state = lazyListState,
                contentPadding = PaddingValues(PADDING_LIST_CONTENT_EDGE),
                verticalArrangement = Arrangement.spacedBy(SPACE_LIST_CONTENT_VERTICAL),
                horizontalAlignment = Alignment.CenterHorizontally,
                userScrollEnabled = true
            ) {
                itemsIndexed(tvInputList) { index, item ->
                    TvInputButton(
                        modifier = Modifier,
                        item = item,
                        onShortClick = {
                            viewModel.setSelectedItemIndex(index)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            OutlinedCard(
                onClick = {
                    viewModel.getSelectedTvInputInfo()?.let { tvInputInfo ->
                        openInputActivity(context, tvInputInfo)
                    } ?: run {
                        Toast.makeText(
                            context,
                            R.string.select_input_first,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                viewModel.getSelectedTvInputInfo()?.let { tvInputInfo ->
                                    openInputActivity(context, tvInputInfo)
                                }
                            }
                        )
                    },
                shape = RectangleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color.DarkGray
                ),
                border = BorderStroke(
                    2.dp,
                    if (isFocused || isHovered) Color.White else Color.Transparent
                ),
                interactionSource = interactionSource
            ) {
                AndroidViewBinding(
                    factory = TvViewLayoutBinding::inflate,
                    modifier = Modifier
                        .focusable(enabled = false),
                    onRelease = {
                        tvView.reset()
                    },
                    update = {
                        val tvInputInfo = viewModel.getSelectedTvInputInfo()
                        if (tvInputInfo != null) {
                            val tvInputId = tvInputInfo.id
                            val tvInputUri =
                                TvContract.buildChannelUriForPassthroughInput(tvInputId)
                            tvView.tune(tvInputId, tvInputUri)
                        }
                    }
                )
            }
        }
    }
}

private fun openInputActivity(context: Context, tvInputInfo: TvInputInfo) {
    val intent = Intent(context, InputActivity::class.java).apply {
        putExtra(InputActivity.EXTRA_INPUT_ID, tvInputInfo.id)
        putExtra(
            InputActivity.EXTRA_CHANNEL_URI,
            TvContract.buildChannelUriForPassthroughInput(tvInputInfo.id)
        )
    }
    context.startActivity(intent)
}