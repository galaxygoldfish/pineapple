package com.pineapple.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun FilterBottomSheet(
    timePeriod: MutableState<String>,
    sortType: MutableState<String>
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onSecondary)
            .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(top = 15.dp)
                .size(width = 40.dp, height = 5.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clip(RoundedCornerShape(50.dp))
        ) {  }

    }
}