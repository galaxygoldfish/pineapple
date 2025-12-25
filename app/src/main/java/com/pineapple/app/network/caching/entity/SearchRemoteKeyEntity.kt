package com.pineapple.app.network.caching.entity

import androidx.room.Entity

@Entity(
    tableName = "search_remote_keys",
    primaryKeys = ["query", "postId"]
)
data class SearchRemoteKeyEntity(
    val query: String,
    val postId: String,
    val prevKey: String?,
    val nextKey: String?
)
