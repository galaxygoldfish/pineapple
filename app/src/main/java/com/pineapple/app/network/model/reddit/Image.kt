package com.pineapple.app.network.model.reddit

data class Image (
    val source: ResizedIcon,
    val resolutions: ArrayList<ResizedIcon>
)

