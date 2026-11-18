package com.github.honqout.tvlauncher3.ui.settings.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.data.model.button.ButtonModel
import com.github.honqout.tvlauncher3.data.model.settings.SlideSettingModel
import com.github.honqout.tvlauncher3.data.model.string.ResourceStr
import com.github.honqout.tvlauncher3.ui.general.components.dialog.AlertDialogTv

@Composable
fun SlideDialog(
    item: SlideSettingModel,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var progress by remember { mutableFloatStateOf(item.progress) }

    AlertDialogTv(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = item.title.asComposableString()
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = progress.toString()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Slider(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    value = progress,
                    onValueChange = { progress = it },
                    valueRange = item.range.first.toFloat()..item.range.last.toFloat(),
                    steps = maxOf(0, item.range.last - item.range.first - 1)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.range.first.toString()
                    )

                    Text(
                        text = item.range.last.toString()
                    )
                }
            }
        },
        confirmButton = ButtonModel(
            text = ResourceStr.StrFromResource(R.string.confirm),
            onClick = { onConfirm(progress) }
        ),
        dismissButton = ButtonModel(
            text = ResourceStr.StrFromResource(R.string.cancel),
            onClick = { onDismiss() }
        )
    )
}