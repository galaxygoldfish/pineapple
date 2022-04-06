package com.pineapple.app.model.reddit

data class PostListing(
    var kind: String,
    var data: ListingItem<PostItem>
)
