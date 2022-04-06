package com.pineapple.app.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun TextOnlyTextField(
    textFieldValue: TextFieldValue,
    hint: String,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle
) {
    Box(
        modifier = modifier
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = { value ->
                onValueChange(value)
            },
            textStyle = textStyle.plus(TextStyle(color = MaterialTheme.colorScheme.onBackground)),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground)
        )
        if (textFieldValue.text.isEmpty()) {
            Text(
                text = hint,
                style = textStyle,
                color = MaterialTheme.colorScheme.onSurface.copy(0.5F)
            )
        }
    }
}
