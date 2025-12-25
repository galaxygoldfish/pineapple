package com.pineapple.app.network.caching.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val parentId: String?,
    val author: String?,
    val body: String?,
    val bodyHtml: String?,
    val ups: Int?,
    val sortKey: Int,
    val depth: Int = 0,
    val replyCount: Int = 0,
    val createdUtc: Long? = null,
    val saved: Boolean? = null,
    val likes: Boolean? = null,
    val permalink: String? = null
)
