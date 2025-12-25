package com.pineapple.app.network.model.reddit

data class ListingBase<T>(
    var kind: String,
    var data: ListingItem<T>
)
