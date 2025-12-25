package com.pineapple.app.network.model.reddit

data class PostListing(
    var kind: String,
    var data: ListingItem<PostItem>
)
