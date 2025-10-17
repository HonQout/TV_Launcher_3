package com.android.tvlauncher3.view.text

import android.text.format.DateUtils
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.android.tvlauncher3.activity.ui.viewmodel.MainViewModel

@Composable
fun DateAndWeekdayText(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    fontSize: TextUnit = 12.sp
) {
    val context = LocalContext.current
    val currentTime by viewModel.currentTime.collectAsState()

    val formattedDate = remember(currentTime) {
        DateUtils.formatDateTime(
            context,
            currentTime,
            DateUtils.FORMAT_NO_YEAR or
                    DateUtils.FORMAT_SHOW_DATE or
                    DateUtils.FORMAT_SHOW_WEEKDAY or
                    DateUtils.FORMAT_ABBREV_WEEKDAY
        )
    }

    Text(
        text = formattedDate,
        modifier = modifier,
        color = color,
        fontSize = fontSize
    )
}