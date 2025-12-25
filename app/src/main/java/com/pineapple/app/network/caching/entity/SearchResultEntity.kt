package com.pineapple.app.network.caching.entity

import androidx.room.Entity

@Entity(
    tableName = "search_results",
    primaryKeys = ["query", "postId"]
)
data class SearchResultEntity(
    val query: String,
    val postId: String,
    val sortKey: Int
)
