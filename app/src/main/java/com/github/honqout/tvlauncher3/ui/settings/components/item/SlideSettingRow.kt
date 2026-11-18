package com.github.honqout.tvlauncher3.ui.settings.components.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.honqout.tvlauncher3.data.model.settings.SlideSettingModel
import com.github.honqout.tvlauncher3.ui.theme.FONT_SIZE_M
import com.github.honqout.tvlauncher3.ui.theme.SettingRowContainer

@Composable
fun SlideSettingRow(
    item: SlideSettingModel,
    onClick: (SlideSettingModel) -> Unit = {},
    onSlide: (Int) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    OutlinedCard(
        onClick = { onClick(item) },
        modifier = Modifier
            .padding(vertical = 6.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick(item) }
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SettingRowContainer
        ),
        border = BorderStroke(
            2.dp,
            if (isFocused || isHovered) Color.White else Color.Transparent
        ),
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 72.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .focusable(false),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingRowHead(item = item)

            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .focusable(enabled = false),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.progress.toString(),
                    modifier = Modifier
                        .widthIn(max = 120.dp),
                    color = Color.LightGray,
                    fontSize = FONT_SIZE_M,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.width(2.dp))

                SettingRowTail()
            }
        }
    }
}