package com.pineapple.app.network.model.reddit

import com.google.gson.JsonElement

data class CommentPreData(
    var kind: String,
    var data: CommentData
)

data class CommentData(
    var author: String?,
    var subreddit: String?,
    var id: String,
    var ups: Long?,
    var body: String?,
    var body_html: String,
    var permalink: String,
    var replies: JsonElement? = null,
    var created_utc: Double? = null,
    var saved: Boolean? = null,
    var likes: Boolean? = null
)

data class CommentDataNull(
    var author: String,
    var subreddit: String,
    var id: String,
    var ups: Long,
    var body: String?,
    var body_html: String,
    var permalink: String,
    var link_title: String? = null
)