package com.pineapple.app.model

data class Image (
    val source: ResizedIcon,
    val resolutions: List<ResizedIcon>,
    val variants: MediaEmbed,
    val id: String
)

