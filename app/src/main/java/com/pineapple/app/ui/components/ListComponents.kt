@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.pineapple.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pineapple.app.ui.theme.ExtraLargeCornerRadius
import com.pineapple.app.ui.theme.ExtraSmallCornerRadius

/**
 * A representation of a list item in the [TonalActionSectionList]
 */
data class TonalActionSectionItem(
    val text: String,
    val icon: Painter,
    val contentDescription: String,
    val onCLick: () -> Unit = { },
    val iconSize: Dp = 24.dp,
    val shouldTintIcon: Boolean = true
)

/**
 * List of clickable options styled as tonal cards, with rounding applied to first and last
 * items but not inner elements to replicate the Material 3 style used in the system settings app
 * @param items The list of [TonalActionSectionItem] to display
 * @param modifier The Modifier to be applied to this component
 * @param singleSelect Whether only a single item can be selected at a time
 * @param selectedIndexInitial The index of the initially selected item, if [singleSelect] is true
 * @param onSelectChange Callback invoked when the selected item changes, providing the new index
 * and corresponding [TonalActionSectionItem] (in that order)
 */
@Composable
fun TonalActionSectionList(
    items: List<TonalActionSectionItem>,
    modifier: Modifier = Modifier,
    singleSelect: Boolean = false,
    selectedIndexInitial: Int = 0,
    onSelectChange: (Int, TonalActionSectionItem) -> Unit = { _, _ -> },
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    listItemContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    listItemContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(selectedIndexInitial) }
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = containerColor
    ) {
        Column {
            items.forEachIndexed { index, item ->
                val isSelected = singleSelect && selectedIndex == index

                val containerColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        listItemContainerColor
                    },
                    label = "containerColor",
                    animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
                )
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        listItemContentColor
                    },
                    label = "contentColor",
                    animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
                )
                val cornerRadius by animateDpAsState(
                    targetValue = if (isSelected) ExtraLargeCornerRadius else ExtraSmallCornerRadius,
                    label = "cornerRadius",
                    animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
                )
                val animatedShape = RoundedCornerShape(cornerRadius)

                ListItem(
                    headlineContent = {
                        Text(item.text)
                    },
                    leadingContent = {
                        if (item.shouldTintIcon) {
                            Icon(
                                painter = item.icon,
                                contentDescription = item.contentDescription,
                                tint = contentColor
                            )
                        } else {
                            Image(
                                painter = item.icon,
                                contentDescription = item.contentDescription,
                                modifier = Modifier.clip(CircleShape)
                                    .size(item.iconSize)
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(bottom = if (index == items.lastIndex) 0.dp else 3.dp)
                        .clip(animatedShape)
                        .clickable {
                            if (singleSelect) {
                                if (selectedIndex != index) {
                                    selectedIndex = index
                                    onSelectChange(index, item)
                                }
                            }
                            item.onCLick()
                        },
                    colors = ListItemDefaults.colors(
                        containerColor = containerColor,
                        contentColor = contentColor
                    )
                )
            }
        }
    }
}