@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.pineapple.app.ui.modal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pineapple.app.R
import com.pineapple.app.consts.PostFilterSort
import com.pineapple.app.consts.PostFilterTime
import com.pineapple.app.ui.components.TonalActionSectionItem
import com.pineapple.app.ui.components.TonalActionSectionList

/**
 * Modal bottom sheet that allows users to filter and sort a list of posts in the same style
 * that is available in reddit (hot, new, top etc. and by day, week, month, etc.)
 * @param currentTimeSelection The currently selected time filter
 * @param currentSortSelection The currently selected sort filter
 * @param onDismissRequest Callback invoked when the sheet is dismissed, passing in the time and
 *                         sort selections (in that order)
 * @see [PostFilterSort] and [PostFilterTime]
 */
@Composable
fun SortPostSheet(
    currentTimeSelection: String,
    currentSortSelection: String,
    onDismissRequest: (String, String) -> Unit
) {
    var selectedSortType by rememberSaveable { mutableStateOf(currentSortSelection) }
    var selectedSortTime by rememberSaveable { mutableStateOf(currentTimeSelection) }
    val showExtended = selectedSortType == PostFilterSort.SORT_TOP
            || selectedSortType == PostFilterSort.SORT_CONTROVERSIAL
    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest(selectedSortTime, selectedSortType)
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = stringResource(R.string.sort_sheet_sort_header),
                style = MaterialTheme.typography.bodyMedium,
            )
            TonalActionSectionList(
                items = listOf(
                    TonalActionSectionItem(
                        text = stringResource(R.string.sort_sheet_hot),
                        icon = painterResource(R.drawable.ic_fire),
                        contentDescription = stringResource(R.string.ic_fire_cdesc)
                    ),
                    TonalActionSectionItem(
                        text = stringResource(R.string.sort_sheet_new),
                        icon = painterResource(R.drawable.ic_shine),
                        contentDescription = stringResource(R.string.ic_shine_cdesc)
                    ),
                    TonalActionSectionItem(
                        text = stringResource(R.string.sort_sheet_rising),
                        icon = painterResource(R.drawable.ic_trending),
                        contentDescription = stringResource(R.string.ic_trending_cdesc)
                    ),
                    TonalActionSectionItem(
                        text = stringResource(R.string.sort_sheet_controversial),
                        icon = painterResource(R.drawable.ic_angry),
                        contentDescription = stringResource(R.string.ic_angry_cdesc)
                    ),
                    TonalActionSectionItem(
                        text = stringResource(R.string.sort_sheet_top),
                        icon = painterResource(R.drawable.ic_arrow_up),
                        contentDescription = stringResource(R.string.ic_arrow_up_cdesc)
                    )
                ),
                singleSelect = true,
                selectedIndexInitial = when (currentSortSelection) {
                    PostFilterSort.SORT_HOT -> 0
                    PostFilterSort.SORT_NEW -> 1
                    PostFilterSort.SORT_RISING -> 2
                    PostFilterSort.SORT_CONTROVERSIAL -> 3
                    PostFilterSort.SORT_TOP -> 4
                    else -> 0
                },
                onSelectChange = { index, item ->
                    selectedSortType = when (index) {
                        0 -> {
                            selectedSortTime = PostFilterTime.TIME_DAY
                            PostFilterSort.SORT_HOT
                        }
                        1 -> {
                            selectedSortTime = PostFilterTime.TIME_DAY
                            PostFilterSort.SORT_NEW
                        }
                        2 -> {
                            selectedSortTime = PostFilterTime.TIME_DAY
                            PostFilterSort.SORT_RISING
                        }
                        3 -> PostFilterSort.SORT_CONTROVERSIAL
                        4 -> PostFilterSort.SORT_TOP
                        else -> PostFilterSort.SORT_HOT
                    }
                },
                modifier = Modifier.padding(
                    top = 15.dp,
                    bottom = animateDpAsState(targetValue = if (showExtended) 0.dp else 15.dp).value
                )
            )
            AnimatedVisibility(
                visible = showExtended,
                modifier = Modifier.fillMaxWidth(),
                enter = fadeIn(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec())
                        + expandVertically(animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()),
                exit = fadeOut(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec())
                        + shrinkVertically(animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec())
            ) {
                Column(modifier = Modifier.padding(bottom = 15.dp)) {
                    Text(
                        text = stringResource(R.string.sort_sheet_time_header),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    TonalActionSectionList(
                        items = listOf(
                            TonalActionSectionItem(
                                text = stringResource(R.string.sort_sheet_day),
                                icon = painterResource(R.drawable.ic_calendar_day),
                                contentDescription = stringResource(R.string.ic_calendar_day_cdesc)
                            ),
                            TonalActionSectionItem(
                                text = stringResource(R.string.sort_sheet_week),
                                icon = painterResource(R.drawable.ic_week),
                                contentDescription = stringResource(R.string.ic_week_cdesc)
                            ),
                            TonalActionSectionItem(
                                text = stringResource(R.string.sort_sheet_month),
                                icon = painterResource(R.drawable.ic_calendar_month),
                                contentDescription = stringResource(R.string.ic_calendar_month_cdesc)
                            ),
                            TonalActionSectionItem(
                                text = stringResource(R.string.sort_sheet_year),
                                icon = painterResource(R.drawable.ic_hourglass),
                                contentDescription = stringResource(R.string.ic_hourglass_cdesc)
                            ),
                            TonalActionSectionItem(
                                text = stringResource(R.string.sort_sheet_all),
                                icon = painterResource(R.drawable.ic_history),
                                contentDescription = stringResource(R.string.ic_history_cdesc)
                            )
                        ),
                        singleSelect = true,
                        selectedIndexInitial = when (currentTimeSelection) {
                            PostFilterTime.TIME_DAY -> 0
                            PostFilterTime.TIME_WEEK -> 1
                            PostFilterTime.TIME_MONTH -> 2
                            PostFilterTime.TIME_YEAR -> 3
                            PostFilterTime.TIME_ALL -> 4
                            else -> 0
                        },
                        onSelectChange = { index, item ->
                            selectedSortTime = when (index) {
                                0 -> PostFilterTime.TIME_DAY
                                1 -> PostFilterTime.TIME_WEEK
                                2 -> PostFilterTime.TIME_MONTH
                                3 -> PostFilterTime.TIME_YEAR
                                4 -> PostFilterTime.TIME_ALL
                                else -> PostFilterTime.TIME_DAY
                            }
                        },
                        modifier = Modifier.padding(top = 15.dp)
                    )
                }
            }
        }
    }
}