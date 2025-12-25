package com.pineapple.app.network.model.reddit

data class UserAboutListing(
    var kind: String,
    var data: UserAbout
)

data class UserAbout(
    var id: String? = null,
    var snoovatar_img: String? = null,
    var icon_img: String? = null,
    var name: String? = null,
    var subreddit: UserSubredditData? = null,
    var is_gold: Boolean? = null,
    var total_karma: Long? = null,
    var awardee_karma: Long? = null,
    var link_karma: Long? = null,
    var awarder_karma: Long? = null,
    var comment_karma: Long? = null,
    var has_verified_email: Boolean? = null,
    var accept_chats: Boolean? = null,
    var created_utc: Long? = null,
    var accept_followers: Boolean? = null,
    var accept_pms: Boolean? = null,
    var verified: Boolean? = null
)
