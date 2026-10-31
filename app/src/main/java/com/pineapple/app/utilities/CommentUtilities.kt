package com.pineapple.app.utilities

import com.pineapple.app.network.caching.entity.CommentEntity

/**
 * The name of the subreddit (without the r/ prefix) that a comment was made in, taken from its
 * permalink (e.g. "/r/android/comments/abc/title/def/" -> "android")
 */
fun CommentEntity.subredditName(): String? {
    val parts = permalink?.split("/") ?: return null
    return if (parts.getOrNull(1) == "r") parts.getOrNull(2)?.takeIf { it.isNotBlank() } else null
}
