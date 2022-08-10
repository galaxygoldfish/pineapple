package com.pineapple.app.model.reddit

data class CommentPreData(
    var kind: String,
    var data: CommentData
)

data class CommentPreDataNull(
    var kind: String,
    var data: CommentDataNull
)

data class CommentData(
    var author: String,
    var subreddit: String,
    var id: String,
    var ups: Long,
    var body: String?,
    var body_html: String,
    var permalink: String,
    var replies: CommentListingNull? = null
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