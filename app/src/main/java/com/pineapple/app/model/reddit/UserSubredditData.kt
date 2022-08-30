package com.pineapple.app.model.reddit

data class UserSubredditData(
    var banner_img: String,
    var display_name: String,
    var over_18: Boolean,
    var icon_img: String,
    var public_description: String,
    var subreddit_type: String,
    var user_is_subscriber: Boolean,
    var display_name_prefixed: String,
    var is_default_icon: Boolean
)
