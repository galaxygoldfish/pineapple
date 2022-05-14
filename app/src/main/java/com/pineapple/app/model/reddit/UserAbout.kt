package com.pineapple.app.model.reddit

data class UserAboutListing(
    var kind: String,
    var data: UserAbout
)

data class UserAbout(
    var id: String,
    var snoovatar_img: String?,
    var icon_img: String?,
    var name: String
)
