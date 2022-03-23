package com.pineapple.app.model.reddit

data class ListingItem(
    var after: String,
    var before: String?,
    var dist: Int,
    var modhash: String,
    var children: List<PostItem>
)
