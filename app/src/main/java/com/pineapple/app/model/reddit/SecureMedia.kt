package com.pineapple.app.model.reddit

data class SecureMedia(
    var reddit_video: RedditVideo
)

data class RedditVideo(
    var bitrate_kbps: Long,
    var fallback_url: String,
    var height: Long,
    var width: Long,
    var hls_url: String,
    var is_gif: Boolean
)