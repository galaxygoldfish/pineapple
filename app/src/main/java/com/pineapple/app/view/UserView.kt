package com.pineapple.app.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pineapple.app.components.SubredditRichHeader
import com.pineapple.app.model.reddit.UserAbout
import com.pineapple.app.theme.PineappleTheme
import com.pineapple.app.util.surfaceColorAtElevation
import com.pineapple.app.viewmodel.UserViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UserView(navController: NavController, user: String) {
    val viewModel: UserViewModel = viewModel()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var currentUserInfo by remember { mutableStateOf<UserAbout?>(null) }
    LaunchedEffect(key1 = user) {
        currentUserInfo = viewModel.requestUserDetails(user)
    }
    PineappleTheme {
        ModalBottomSheetLayout(
            sheetContent = { Text("dd") },
            sheetBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
            sheetShape = RoundedCornerShape(topEnd = 15.dp, topStart = 15.dp),
            scrimColor = Color.Black.copy(0.4F)
        ) {
            Scaffold(
                topBar = {
                    SmallTopAppBar(
                        title = {

                        }
                    )
                }
            ) {
                Column(
                    modifier = Modifier.padding(
                        top = it.calculateTopPadding(),
                        bottom = it.calculateBottomPadding(),
                        start = it.calculateStartPadding(LayoutDirection.Ltr),
                        end = it.calculateEndPadding(LayoutDirection.Ltr)
                    )
                ) {

                }
            }
        }
    }
}