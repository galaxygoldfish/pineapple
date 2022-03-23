package com.pineapple.app.model.reddit

data class Image (
    val source: ResizedIcon,
    val resolutions: List<ResizedIcon>,
    val variants: MediaEmbed,
    val id: String
)

