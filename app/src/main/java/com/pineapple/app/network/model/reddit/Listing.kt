package com.pineapple.app.network.model.reddit

data class Listing<T>(
    val kind: String,
    val data: ListingItem<T>
)
