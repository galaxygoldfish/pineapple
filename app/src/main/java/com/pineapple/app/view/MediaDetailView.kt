package com.pineapple.app.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pineapple.app.components.ImageGifControls
import com.pineapple.app.components.MultiTypeMediaView
import com.pineapple.app.components.VideoControls
import com.pineapple.app.theme.PineappleTheme
import com.pineapple.app.util.calculateRatioHeight
import com.pineapple.app.util.getViewModel
import com.pineapple.app.viewmodel.MediaDetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun MediaDetailView(
    navController: NavController,
    mediaType: String,
    encodedUrl: String,
    domain: String? = null,
    titleText: String
) {
    val viewModel = LocalContext.current.getViewModel(MediaDetailViewModel::class.java)
    val context = LocalContext.current
    rememberSystemUiController().setSystemBarsColor(Color.Black)
    PineappleTheme {
        Column {
            MultiTypeMediaView(
                mediaHint = mediaType,
                url = URLDecoder.decode(encodedUrl),
                richDomain = domain,
                gfycatService = viewModel.gfycatService,
                modifier = Modifier.fillMaxSize()
                    .background(Color.Black),
                playerControls = {
                    VideoControls(
                        player = it,
                        fullscreen = true,
                        onExpand = { navController.popBackStack() },
                        onBackPress = { navController.popBackStack() },
                        onDownload = {  },
                        postTitle = titleText
                    )
                },
                detailedView = true,
                modifierVideo = {
                    Modifier.align(Alignment.Center)
                },
                imageControls = {
                    ImageGifControls(
                        postTitle = titleText,
                        onDownload = {
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.downloadImage(URLDecoder.decode(encodedUrl), context)
                            }
                        },
                        onBackPress = { navController.popBackStack() }
                    )
                }
            )
        }
    }
}