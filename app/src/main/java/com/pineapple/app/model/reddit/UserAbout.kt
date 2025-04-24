package com.pineapple.app.model.reddit

data class UserAboutListing(
    var kind: String,
    var data: UserAbout
)

data class UserAbout(
    var id: String,
    var snoovatar_img: String?,
    var icon_img: String?,
    var name: String?,
    var subreddit: UserSubredditData?,
    var is_gold: Boolean,
    var total_karma: Long,
    var awardee_karma: Long,
    var link_karma: Long,
    var awarder_karma: Long,
    var comment_karma: Long,
    var has_verified_email: Boolean,
    var accept_chats: Boolean,
    var created_utc: Long,
    var accept_followers: Boolean,
    var accept_pms: Boolean,
    var verified: Boolean
)
