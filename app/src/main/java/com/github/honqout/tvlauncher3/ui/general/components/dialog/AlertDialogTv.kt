package com.github.honqout.tvlauncher3.ui.general.components.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.honqout.tvlauncher3.data.model.button.ButtonModel
import com.github.honqout.tvlauncher3.ui.theme.FONT_SIZE_M

@Composable
fun AlertDialogTv(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    shape: Shape = RoundedCornerShape(16.dp),
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)?,
    content: @Composable (() -> Unit)?,
    confirmButton: ButtonModel?,
    neutralButton: ButtonModel? = null,
    dismissButton: ButtonModel? = null
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            usePlatformDefaultWidth = true
        )
    ) {
        Card(
            modifier = modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            colors = CardDefaults.cardColors().copy(
                containerColor = Color.DarkGray
            ),
            shape = shape
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                icon?.let {
                    icon()

                    Spacer(modifier = Modifier.height(8.dp))
                }

                title?.let {
                    title()

                    Spacer(modifier = Modifier.height(8.dp))
                }

                content?.let {
                    content()

                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        dismissButton,
                        neutralButton,
                        confirmButton
                    ).forEach { buttonModel ->
                        buttonModel?.let {
                            val interactionSource = remember { MutableInteractionSource() }
                            val isFocused by interactionSource.collectIsFocusedAsState()
                            val isHovered by interactionSource.collectIsHoveredAsState()

                            OutlinedButton(
                                onClick = { buttonModel.onClick() },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .weight(weight = 1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors().copy(
                                    containerColor = Color.Gray
                                ),
                                border = BorderStroke(
                                    width = 2.dp,
                                    color = if (isFocused || isHovered) Color.White else Color.Transparent
                                ),
                                interactionSource = interactionSource
                            ) {
                                Text(
                                    text = buttonModel.text.asComposableString(),
                                    color = Color.White,
                                    fontSize = FONT_SIZE_M,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}