package com.pineapple.app.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.pineapple.app.R
import kotlinx.coroutines.launch
import java.io.FileDescriptor

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterBottomSheet(
    timePeriod: MutableState<String>,
    sortType: MutableState<String>,
    bottomSheetState: ModalBottomSheetState
) {
    val tempTimePeriod = remember { mutableStateOf(timePeriod.value) }
    val tempSortType = remember { mutableStateOf(sortType.value) }
    val coroutineScope = rememberCoroutineScope()
    val sortChips = listOf(
        Triple(R.string.home_filter_sheet_sort_hot, R.drawable.ic_local_fire_department, R.string.ic_local_fire_department_content_desc),
        Triple(R.string.home_filter_sheet_sort_new, R.drawable.ic_auto_awesome, R.string.ic_auto_awesome_content_desc),
        Triple(R.string.home_filter_sheet_sort_rising, R.drawable.ic_trending_up, R.string.ic_trending_up_content_desc),
        Triple(R.string.home_filter_sheet_sort_top, R.drawable.ic_arrow_upward, R.string.ic_arrow_upward_content_desc),
        Triple(R.string.home_filter_sheet_sort_controversial, R.drawable.ic_sentiment_extremely_dissatisfied, R.string.ic_sentiment_extremely_dissatisfied_content_desc)
    )
    val timeChips = listOf(
        Triple(R.string.home_filter_sheet_time_hour, R.drawable.ic_hourglass_bottom, R.string.ic_hourglass_bottom_content_desc),
        Triple(R.string.home_filter_sheet_time_day, R.drawable.ic_nights_stay, R.string.ic_nights_stay_content_desc),
        Triple(R.string.home_filter_sheet_time_week, R.drawable.ic_av_timer, R.string.ic_av_timer_content_desc),
        Triple(R.string.home_filter_sheet_time_month, R.drawable.ic_event_note, R.string.ic_event_note_content_desc),
        Triple(R.string.home_filter_sheet_time_year, R.drawable.ic_event_available, R.string.ic_event_available_content_desc),
        Triple(R.string.home_filter_sheet_time_all, R.drawable.ic_atr_dots, R.string.ic_atr_dots_content_desc)
    )
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .padding(top = 15.dp)
                .clip(RoundedCornerShape(5.dp))
                .size(width = 100.dp, height = 5.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .align(Alignment.CenterHorizontally)
        ) { }
        Text(
            text = stringResource(id = R.string.home_filter_sheet_sort_header),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 15.dp, start = 20.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 15.dp, top = 10.dp)
        ) {
            sortChips.forEach { item ->
                SortChipItem(item = item, sortVar = tempSortType)
            }
        }
        if (tempSortType.value.equals("Top", true) ||
            tempSortType.value.equals("Controversial", true)) {
            Text(
                text = stringResource(id = R.string.home_filter_sheet_time_header),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 20.dp, top = 15.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 10.dp, top = 10.dp)
            ) {
                timeChips.forEach { item ->
                    SortChipItem(item = item, sortVar = tempTimePeriod)
                }
            }
        }
        FilledTonalButton(
            onClick = {
                sortType.value = tempSortType.value
                timePeriod.value = tempTimePeriod.value
                coroutineScope.launch {
                    bottomSheetState.hide()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = stringResource(id = R.string.ic_check_content_desc)
            )
            Text(
                text = stringResource(id = R.string.home_filter_sheet_apply_button),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start = 15.dp)
            )
        }
    }
}

@Composable
private fun SortChipItem(
    item: Triple<Int, Int, Int>,
    sortVar: MutableState<String>
) {
    val chipText = stringResource(id = item.first)
    val allTimeText = stringResource(id = R.string.home_filter_sheet_time_all)
    Chip(
        text = chipText,
        selected = sortVar.value.let {
            it.equals(chipText, true)
                    || (chipText == allTimeText && it.equals(
                allTimeText.split(" ")[0], true
            ))
        },
        icon = painterResource(id = item.second),
        contentDescription = stringResource(id = item.third),
        unselectedBackground = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        onClick = {
            sortVar.value = (if (chipText != allTimeText) {
                chipText.toLowerCase(Locale.current)
            } else {
                Log.e("DD", allTimeText.split(" ")[0].toLowerCase(Locale.current))
                allTimeText.split(" ")[0].toLowerCase(Locale.current)
            })
        },
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
fun DialogContainer(
    title: String = "",
    actionButtons: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
    ) {
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 24.dp, start = 24.dp)
            )
        }
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            content.invoke()
        }
        actionButtons?.let {
            Row(
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                it.invoke(this)
            }
        }
    }
}

@Composable
fun DialogListItem(
    text: String,
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable { onClick.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 24.dp),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}