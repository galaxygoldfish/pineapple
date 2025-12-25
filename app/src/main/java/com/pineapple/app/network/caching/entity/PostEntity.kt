package com.pineapple.app.network.caching.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String?,
    val subreddit: String?,
    val createdUtc: Long,
    val ups: Int?,
    val thumbnail: String?,
    val permalink: String,
    val url: String?,
    val previewImageUrl: String?,
    val previewWidth: Long?,
    val previewHeight: Long?,
    val sortKey: Int,
    val saved: Boolean? = null,
    val likes: Boolean? = null,
    val selftext: String? = null
)