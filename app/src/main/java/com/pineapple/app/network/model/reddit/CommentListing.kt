package com.pineapple.app.network.model.reddit

data class CommentListing(
    var kind: String,
    var data: ListingItem<CommentPreData>
)

data class CommentListingNull(
    var kind: String,
    var data: ListingItem<Any>
)
