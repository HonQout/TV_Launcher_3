package com.github.honqout.tvlauncher3.ui.settings.components.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.data.model.button.ButtonModel
import com.github.honqout.tvlauncher3.data.model.settings.SingleChoiceSettingModel
import com.github.honqout.tvlauncher3.data.model.string.ResourceStr
import com.github.honqout.tvlauncher3.ui.general.components.dialog.AlertDialogTv

@Composable
fun SingleChoiceDialog(
    item: SingleChoiceSettingModel,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    var selectedIndex by remember { mutableIntStateOf(item.chosen) }

    AlertDialogTv(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = item.title.asComposableString()
            )
        },
        content = {
            Column {
                item.choices.forEachIndexed { index, choice ->
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .focusable()
                            .clickable { selectedIndex = index }
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = choice.content
                        )

                        RadioButton(
                            selected = selectedIndex == index,
                            onClick = { selectedIndex = index }
                        )
                    }
                }
            }
        },
        confirmButton = ButtonModel(
            text = ResourceStr.StrFromResource(R.string.confirm),
            onClick = { onConfirm(selectedIndex) }
        ),
        dismissButton = ButtonModel(
            text = ResourceStr.StrFromResource(R.string.cancel),
            onClick = { onDismiss() }
        )
    )
}