package com.pineapple.app.network.caching.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val postId: String,
    val prevKey: String?,
    val nextKey: String?
)