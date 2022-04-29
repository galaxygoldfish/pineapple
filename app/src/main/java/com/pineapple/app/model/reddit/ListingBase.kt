package com.pineapple.app.model.reddit

data class ListingBase<T>(
    var kind: String,
    var data: ListingItem<T>
)
