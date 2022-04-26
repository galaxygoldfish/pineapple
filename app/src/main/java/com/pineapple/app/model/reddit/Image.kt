package com.pineapple.app.model.reddit

data class Image (
    val source: ResizedIcon,
    val resolutions: ArrayList<ResizedIcon>
)

