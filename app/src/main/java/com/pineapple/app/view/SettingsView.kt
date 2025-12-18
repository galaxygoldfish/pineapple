package com.pineapple.app.view

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pineapple.app.MainActivity
import com.pineapple.app.R
import com.pineapple.app.components.SheetHandle
import com.pineapple.app.theme.PineappleTheme
import com.pineapple.app.theme.themeOptionMap
import com.pineapple.app.util.getPreferences
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
fun SettingsView(navController: NavController) {
    val sharedPreferences = LocalContext.current.getPreferences()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val topAppBarBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    PineappleTheme {
        ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            scrimColor = Color.Black.copy(0.3F),
            sheetShape = RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp),
            sheetBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
            sheetContent = {
                SettingsThemeApplyDialog()
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = stringResource(id = R.string.settings_title_bar_text))
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_arrow_back),
                                    contentDescription = stringResource(id = R.string.ic_arrow_back_content_desc)
                                )
                            }
                        },
                        scrollBehavior = topAppBarBehavior
                    )
                }
            ) {
                LazyColumn(
                    modifier = Modifier.padding(
                        top = it.calculateTopPadding(),
                        bottom = it.calculateBottomPadding(),
                        start = it.calculateStartPadding(LayoutDirection.Ltr),
                        end = it.calculateEndPadding(LayoutDirection.Ltr)
                    )
                        .nestedScroll(topAppBarBehavior.nestedScrollConnection)
                ) {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
                        item {
                            SettingsPreferenceItem(
                                icon = painterResource(id = R.drawable.ic_palette),
                                contentDescription = stringResource(id = R.string.ic_palette_content_desc),
                                title = stringResource(id = R.string.settings_theme_color_item_title),
                                subtitle = stringResource(id = R.string.settings_theme_color_item_subtitle),
                                key = "THEME_ITEM",
                                useSwitch = false
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 15.dp)
                            ) {
                                val themeOptions = themeOptionMap()
                                val currentTheme = sharedPreferences.getString("APP_THEME", "DEEP_SEA")
                                themeOptions.remove(currentTheme)?.apply {
                                    SettingsThemeCard(
                                        themeData = this,
                                        key = currentTheme!!,
                                        sheetState = bottomSheetState
                                    )
                                }
                                themeOptions.forEach { (key, value) ->
                                    SettingsThemeCard(
                                        themeData = value,
                                        key = key,
                                        sheetState = bottomSheetState
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsThemeApplyDialog() {
    val localContext = LocalContext.current
    SheetHandle()
    Text(
        text = stringResource(id = R.string.settings_theme_restart_app_dialog_title),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(start = 20.dp, top = 10.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
        text = stringResource(id = R.string.settings_theme_restart_app_dialog_subtitle),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    FilledTonalButton(
        onClick = {
            localContext.apply {
                startActivity(Intent(this, MainActivity::class.java))
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_restart),
            contentDescription = stringResource(id = R.string.ic_restart_content_desc)
        )
        Text(
            text = stringResource(id = R.string.settings_theme_restart_app_dialog_button),
            modifier = Modifier.padding(start = 15.dp)
        )
    }
}

@Composable
fun SettingsPreferenceItem(
    icon: Painter,
    contentDescription: String,
    title: String,
    subtitle: String,
    useSwitch: Boolean = false,
    defaultValue: Boolean = false,
    key: String,
    onClick: (() -> Unit)? = null
) {
    val preferences = LocalContext.current.getPreferences()
    var switchState by remember { mutableStateOf(preferences.getBoolean(key, defaultValue)) }
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick?.invoke()
                    if (useSwitch) switchState = !switchState
                }
        ) {
            Icon(
                painter = icon,
                contentDescription = contentDescription,
                modifier = Modifier.padding(start = 20.dp)
            )
            Column(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .weight(1F)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 20.dp)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        top = 5.dp,
                        bottom = 20.dp,
                        end = if (useSwitch) 0.dp else 20.dp
                    )
                )
            }
            if (useSwitch) {
                Switch(
                    checked = switchState,
                    onCheckedChange = {
                        switchState = it
                        preferences.edit().putBoolean(key, it).apply()
                    },
                    enabled = true,
                    modifier = Modifier.padding(end = 20.dp)
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
fun SettingsThemeCard(
    themeData: Triple<ColorScheme, ColorScheme, String>,
    key: String,
    sheetState: ModalBottomSheetState
) {
    val preferences = LocalContext.current.getPreferences()
    val coroutineScope = rememberCoroutineScope()
    val currentThemePalette = (if (isSystemInDarkTheme()) themeData.second else themeData.first)
    val colorList = themeData.let {
        mutableListOf(it.first.primary, it.first.secondary, it.second.primary, it.second.secondary)
    }
    Card(
        onClick = {
            preferences.edit().putString("APP_THEME", key).commit()
            coroutineScope.launch {
                sheetState.show()
            }
        },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = currentThemePalette.secondaryContainer
        ),
        modifier = Modifier.padding(end = 15.dp),
        border = if (preferences.getString("APP_THEME", "DEEP_SEA") == key) {
            BorderStroke(2.dp, currentThemePalette.outline)
        } else BorderStroke(0.dp, Color.Transparent)
    ) {
        Column(modifier = Modifier.padding(vertical = 5.dp)) {
            repeat(2) {
                Row(modifier = Modifier.padding(horizontal = 5.dp)) {
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .padding(5.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(colorList.removeAt(0))
                        )
                    }
                }
            }
        }
        Text(
            text = themeData.third,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(10.dp)
        )
    }
}