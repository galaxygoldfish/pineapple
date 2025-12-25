@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.pineapple.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import com.pineapple.app.ui.theme.FullCornerRadius
import com.pineapple.app.ui.theme.MediumCornerRadius

/**
 * Tonal icon toggle button that animates its shape based off of the checked status, using
 * Material 3 Expressive motion physics
 * @param checked Whether the button is checked or not
 * @param onCheckedChange Lambda that is triggered when the button is clicked
 * @param modifier [Modifier] to be applied to the button
 * @param checkedRadius Corner radius when the button is checked
 * @param uncheckedRadius Corner radius when the button is unchecked
 * @param checkedIcon Icon to be displayed when the button is checked
 * @param uncheckedIcon Icon to be displayed when the button is unchecked
 * @param contentDescription Content description for the icon (both states)
 */
@Composable
fun AnimatedTonalToggleIconButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checkedRadius: Dp = FullCornerRadius,
    uncheckedRadius: Dp = MediumCornerRadius,
    checkedIcon: Painter,
    uncheckedIcon: Painter,
    contentDescription: String,
    colors: IconToggleButtonColors = IconButtonDefaults.filledTonalIconToggleButtonColors()
) {
    val targetRadius = if (checked) checkedRadius else uncheckedRadius
    val shapeRadius by animateDpAsState(
        targetValue = targetRadius,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        label = "shapeRadius"
    )
    FilledTonalIconToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChange,
        shape = RoundedCornerShape(shapeRadius),
        modifier = modifier,
        colors = colors
    ) {
        Icon(
            painter = if (checked) checkedIcon else uncheckedIcon,
            contentDescription = contentDescription
        )
    }
}