package com.pineapple.app.model.reddit

data class Listing<T>(
    val kind: String,
    val data: ListingItem<T>
)
