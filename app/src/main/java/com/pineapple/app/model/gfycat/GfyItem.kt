package com.pineapple.app.model.gfycat

data class GfyItem(
    var gfyId: String,
    var gfyName: String,
    var nsfw: Int,
    var height: Int,
    var width: Int,
    var gifUrl: String
)
