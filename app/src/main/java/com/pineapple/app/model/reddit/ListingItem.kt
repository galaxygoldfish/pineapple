package com.pineapple.app.model.reddit

data class ListingItem<T>(
    var after: String,
    var before: String?,
    var dist: Int,
    var modhash: String,
    var children: List<T>
)
