package com.github.honqout.tvlauncher3.ui.settings.components.item

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import com.github.honqout.tvlauncher3.data.model.settings.SettingModel
import com.github.honqout.tvlauncher3.ui.theme.FONT_SIZE_M
import com.github.honqout.tvlauncher3.ui.theme.FONT_SIZE_S

@Composable
fun SettingRowHead(item: SettingModel) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .focusable(enabled = false),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = item.title.asComposableString(),
            color = Color.White,
            fontSize = FONT_SIZE_M,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )

        item.summary?.let { summary ->
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = summary.asComposableString(),
                color = Color.LightGray,
                fontSize = FONT_SIZE_S,
                maxLines = 1
            )
        }
    }
}