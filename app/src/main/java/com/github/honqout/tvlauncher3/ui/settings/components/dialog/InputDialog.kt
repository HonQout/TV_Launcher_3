package com.github.honqout.tvlauncher3.ui.settings.components.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.honqout.tvlauncher3.R
import com.github.honqout.tvlauncher3.data.model.button.ButtonModel
import com.github.honqout.tvlauncher3.data.model.settings.InputSettingModel
import com.github.honqout.tvlauncher3.data.model.string.ResourceStr
import com.github.honqout.tvlauncher3.ui.general.components.dialog.AlertDialogTv

@Composable
fun InputDialog(
    item: InputSettingModel,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf(item.placeHolder.asString(context)) }

    AlertDialogTv(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = item.title.asComposableString()
            )
        },
        content = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth(),
            )
        },
        confirmButton = ButtonModel(
            text = ResourceStr.StrFromResource(R.string.confirm),
            onClick = { onConfirm(text) }
        ),
        dismissButton = ButtonModel(
            text = ResourceStr.StrFromResource(R.string.cancel),
            onClick = { onDismiss() }
        )
    )
}