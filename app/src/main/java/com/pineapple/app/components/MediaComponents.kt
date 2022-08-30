package com.pineapple.app.components

import android.os.Build.VERSION.SDK_INT
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.Listener
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.pineapple.app.NavDestination
import com.pineapple.app.R
import com.pineapple.app.model.gfycat.GfycatObject
import com.pineapple.app.model.reddit.UserAbout
import com.pineapple.app.model.reddit.UserAboutListing
import com.pineapple.app.network.GfycatNetworkService
import com.pineapple.app.theme.PineappleTheme
import com.pineapple.app.util.toDp
import java.net.URLDecoder

@Composable
fun ExoVideoPlayer(
    url: String,
    previewUrl: String,
    modifier: Modifier,
    modifierVideo: BoxScope.() -> Modifier,
    playerControls: @Composable (MutableState<Player?>) -> Unit,
    expandedView: Boolean = false
) {
    val playerController = remember { mutableStateOf<Player?>(null) }
    var playbackState by remember { mutableStateOf(ExoPlayer.STATE_IDLE) }
    var showPlayerControls by remember { mutableStateOf(true) }
    var showExoPlayer by remember { mutableStateOf(false) }
    var playAutomatically by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .background(Color.Black)
            .clickable {
                showPlayerControls = !showPlayerControls
            }
    ) {
        if (!expandedView) {
            AnimatedVisibility(
                visible = playerController.value == null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(previewUrl)
                            .crossfade(true)
                            .build().data,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillWidth
                    )
                    FilledTonalIconButton(
                        onClick = {
                            showExoPlayer = true
                            playAutomatically = true
                            showPlayerControls = false
                        },
                        modifier = Modifier
                            .size(80.dp)
                            .shadow(elevation = 5.dp, shape = RoundedStarShape(sides = 9))
                            .align(Alignment.Center),
                        shape = RoundedStarShape(sides = 9)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_play_arrow),
                            contentDescription = stringResource(id = R.string.ic_play_arrow_content_desc),
                            modifier = Modifier
                                .padding(end = 2.dp)
                                .size(40.dp)
                        )
                    }
                }
            }
        } else {
            showExoPlayer = true
        }
        AnimatedVisibility(
            visible = showExoPlayer,
            modifier = modifierVideo(this),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AndroidView(
                factory = { context ->
                    StyledPlayerView(context).apply {
                        player = ExoPlayer.Builder(context).build().apply {
                            val dataSourceFactory = DefaultDataSource.Factory(context)
                            val internetVideoItem = MediaItem.fromUri(url)
                            val internetVideoSource = ProgressiveMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(internetVideoItem)
                            addMediaSource(internetVideoSource)
                            prepare()
                            useController = false
                            addListener(object : Listener {
                                override fun onPlaybackStateChanged(state: Int) {
                                    super.onPlaybackStateChanged(state)
                                    playbackState = state
                                    if (playAutomatically && state == STATE_READY) {
                                        play()
                                    }
                                }
                            })
                        }
                        playerController.value = player
                    }
                }
            )
        }
        AnimatedVisibility(
            visible = showPlayerControls && showExoPlayer,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            playerControls(playerController)
        }
    }
}

@Composable
fun GifVideoPlayer(
    modifier: Modifier = Modifier,
    url: String
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    Image(
        painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(context)
                .data(data = url)
                .apply { size(Size.ORIGINAL) }
                .build(),
            imageLoader = imageLoader
        ),
        contentDescription = null,
        modifier = modifier,
    )
}

@Composable
fun MultiTypeMediaView(
    mediaHint: String,
    url: String,
    modifier: Modifier = Modifier,
    modifierVideo: (BoxScope.() -> Modifier) = { Modifier },
    richDomain: String? = null,
    gfycatService: GfycatNetworkService? = null,
    playerControls: @Composable (MutableState<Player?>) -> Unit,
    expandToFullscreen: (() -> Unit)? = null,
    imageControls: (@Composable () -> Unit)? = null,
    previewUrl: String = "",
    expandedView: Boolean = false
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (mediaHint) {
            "image", "link" -> {
                if (url.contains(".gif")) {
                    GifVideoPlayer(
                        url = url,
                        modifier = modifier.clickable {
                            expandToFullscreen?.invoke()
                        }
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(url)
                            .crossfade(true)
                            .build().data,
                        contentDescription = null,
                        modifier = modifier.clickable {
                            expandToFullscreen?.invoke()
                        },
                        contentScale = ContentScale.FillWidth
                    )
                }
                imageControls?.invoke()
            }
            "hosted:video" -> {
                ExoVideoPlayer(
                    url = url.replace("amp;", ""),
                    modifier = modifier,
                    playerControls = playerControls,
                    modifierVideo = modifierVideo,
                    previewUrl = previewUrl,
                    expandedView = expandedView
                )
            }
            "rich:video" -> {
                if (richDomain == "gfycat.com") {
                    gfycatService?.let {
                        var gifInformation by remember { mutableStateOf<GfycatObject?>(null) }
                        LaunchedEffect(true) {
                            gifInformation = it.fetchGif(
                                url.split("https://gfycat.com/")[1]
                            )
                        }
                        if (gifInformation != null) {
                            GifVideoPlayer(
                                url = gifInformation!!.gfyItem.gifUrl,
                                modifier = modifier.clickable {
                                    expandToFullscreen?.invoke()
                                }
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.secondary,
                                    strokeWidth = 3.dp
                                )
                            }
                        }
                    }
                }
                imageControls?.invoke()
            }
        }
    }
}

@Composable
fun VideoControls(
    player: MutableState<Player?>,
    postTitle: String,
    onExpand: () -> Unit,
    onDownload: (() -> Unit)? = null,
    onBackPress: (() -> Unit)? = null,
    fullscreen: Boolean = false
) {
    val mainHandler = Handler(Looper.getMainLooper())
    val context = LocalContext.current
    var videoProgress by remember { mutableStateOf(player.value?.currentPosition) }
    var playPauseIcon by remember {
        mutableStateOf(
            if (player.value?.isPlaying == true) {
                R.drawable.ic_pause
            } else {
                R.drawable.ic_play_arrow
            }
        )
    }
    var bottomControlHeight by remember { mutableStateOf(0.dp) }
    val updateProgress = object : Runnable {
        override fun run() {
            videoProgress = player.value?.currentPosition
            mainHandler.postDelayed(this, 100)
        }
    }
    LaunchedEffect(true) {
        mainHandler.post(updateProgress)
    }
    PineappleTheme(useDarkTheme = if (fullscreen) false else isSystemInDarkTheme()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (fullscreen) {
                        Color.Black.copy(0.3F)
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(0.3F)
                    }
                )
        ) {
            FilledTonalIconButton(
                onClick = {
                    player.value?.prepare()
                    playPauseIcon = if (player.value?.isPlaying == true) {
                        player.value?.pause()
                        R.drawable.ic_play_arrow
                    } else {
                        player.value?.play()
                        R.drawable.ic_pause
                    }
                },
                modifier = Modifier
                    .size(80.dp)
                    .shadow(elevation = 5.dp, shape = RoundedStarShape(sides = 9))
                    .align(Alignment.Center),
                shape = RoundedStarShape(sides = 9)
            ) {
                Icon(
                    painter = painterResource(id = playPauseIcon),
                    contentDescription = stringResource(id = R.string.ic_play_arrow_content_desc),
                    modifier = Modifier
                        .padding(end = 2.dp)
                        .size(40.dp)
                )
            }
            if ((player.value?.contentDuration ?: 0) > 0) {
                FilledTonalIconButton(
                    onClick = { onExpand.invoke() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 15.dp, end = 15.dp)
                        .size(35.dp)
                ) {
                    Icon(
                        painter = painterResource(id = if (fullscreen) {
                            R.drawable.ic_close_fullscreen
                        } else {
                            R.drawable.ic_open_in_full
                        }),
                        contentDescription = stringResource(id = if (fullscreen) {
                            R.string.ic_close_fullscreen_content_desc
                        } else {
                            R.string.ic_open_in_full_content_desc
                        }),
                        modifier = Modifier.size(20.dp)
                    )
                }
                if (fullscreen) {
                    FilledTonalIconButton(
                        onClick = { onDownload?.invoke() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 15.dp, end = 65.dp)
                            .size(35.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_download),
                            contentDescription = stringResource(id = R.string.ic_download_content_desc),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    FilledTonalIconButton(
                        onClick = { onBackPress?.invoke() },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 15.dp, start = 15.dp)
                            .size(35.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = stringResource(id = R.string.ic_arrow_back_content_desc),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = postTitle,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(
                                bottom = bottomControlHeight + 20.dp,
                                start = 15.dp,
                                end = 15.dp
                            ),
                        color = MaterialTheme.colorScheme.surface
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(
                            end = 10.dp,
                            start = 15.dp,
                            bottom = if (fullscreen) 10.dp else 0.dp
                        )
                        .onGloballyPositioned {
                            bottomControlHeight = it.size.height.toDp(context)
                        }
                ) {
                    Text(
                        text =  String.format(
                            format = "%02d:%02d",
                            (videoProgress?.div(1000) ?: 1) / 60,
                            (videoProgress?.div(1000) ?: 1) % 60
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                    Slider(
                        value = videoProgress?.toFloat() ?: 0F,
                        onValueChange = {
                            videoProgress = it.toLong()
                        },
                        onValueChangeFinished = {
                            videoProgress?.let {
                                player.value?.seekTo(it)
                            }
                        },
                        valueRange = 0F..(player.value?.contentDuration?.toFloat() ?: 0F),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.secondaryContainer,
                            activeTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                            inactiveTrackColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ImageGifControls(
    postTitle: String,
    onBackPress: () -> Unit,
    onDownload: () -> Unit
) {
    var showControls by remember { mutableStateOf(true) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { showControls = !showControls }
    ) {
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Scaffold(
                topBar = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilledTonalIconButton(
                            onClick = { onBackPress.invoke() },
                            modifier = Modifier
                                .padding(top = 15.dp, start = 15.dp)
                                .size(35.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_back),
                                contentDescription = stringResource(id = R.string.ic_arrow_back_content_desc),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Row {
                            FilledTonalIconButton(
                                onClick = { onDownload.invoke() },
                                modifier = Modifier
                                    .padding(top = 15.dp, end = 15.dp)
                                    .size(35.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_download),
                                    contentDescription = stringResource(id = R.string.ic_download_content_desc),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            FilledTonalIconButton(
                                onClick = { onBackPress.invoke() },
                                modifier = Modifier
                                    .padding(top = 15.dp, end = 15.dp)
                                    .size(35.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_close_fullscreen),
                                    contentDescription = stringResource(R.string.ic_close_fullscreen_content_desc),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                },
                containerColor = Color.Transparent
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = URLDecoder.decode(postTitle),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(
                                top = it.calculateTopPadding(),
                                bottom = it.calculateBottomPadding() + 20.dp,
                                start = it.calculateStartPadding(LayoutDirection.Ltr) + 15.dp,
                                end = it.calculateEndPadding(LayoutDirection.Ltr) + 15.dp
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun UserAvatarIcon(
    userInfo: UserAboutListing?,
    onClick: () -> Unit
) {
    Box {
        AnimatedVisibility(
            visible = userInfo == null || userInfo.data.subreddit.is_default_icon,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AvatarPlaceholderIcon(
                modifier = Modifier
                    .padding(top = 15.dp, start = 15.dp, end = 10.dp)
                    .clickable {
                        onClick.invoke()
                    }
                    .size(35.dp)
            )
        }
        AnimatedVisibility(
            visible = userInfo != null && !userInfo.data.subreddit.is_default_icon,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(
                        userInfo?.data?.snoovatar_img.toString().ifBlank {
                            userInfo?.data?.icon_img
                        }
                    )
                    .crossfade(true)
                    .build().data,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 15.dp, start = 15.dp, end = 10.dp)
                    .clickable {
                        onClick.invoke()
                    }
                    .size(35.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.FillWidth,
            )
        }
    }
}