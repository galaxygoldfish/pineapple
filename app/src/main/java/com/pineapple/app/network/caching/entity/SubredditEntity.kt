package com.pineapple.app.network.caching.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subreddits")
data class SubredditEntity(
    @PrimaryKey val id: String,          // "t5_xxx" or short id
    val name: String,                    // "androiddev"
    val title: String,
    val iconUrl: String,
    val subscribers: Long,
    val isNsfw: Boolean,
    val isSubscribed: Boolean
)