package com.pineapple.app.utilities

import com.pineapple.app.network.caching.entity.PostEntity
import com.pineapple.app.network.caching.entity.SubredditEntity
import com.pineapple.app.network.caching.entity.UserEntity
import com.pineapple.app.network.model.reddit.Image
import com.pineapple.app.network.model.reddit.PostData
import com.pineapple.app.network.model.reddit.Preview
import com.pineapple.app.network.model.reddit.ResizedIcon
import com.pineapple.app.network.model.reddit.SubredditItem
import com.pineapple.app.network.model.reddit.UserAbout
import com.pineapple.app.network.model.reddit.UserAboutListing

/**
 * Convert a cached PostEntity to a PostData object
 */
fun PostEntity.toPostData(): PostData {
    val source = if (previewImageUrl != null && previewWidth != null && previewHeight != null) {
        ResizedIcon(
            url = previewImageUrl,
            width = previewWidth,
            height = previewHeight
        )
    } else null
    val preview = source?.let {
        Preview(
            images = arrayListOf(
                Image(
                    source = it,
                    resolutions = arrayListOf()
                )
            ) as ArrayList<Image>?
        )
    }
    return PostData(
        id = id.removePrefix("t3_"),
        name = if (id.startsWith("t3_")) id else "t3_$id",
        title = title,
        author = author,
        subreddit = subreddit,
        subredditNamePrefixed = subreddit?.let { "r/$it" },
        createdUTC = createdUtc,
        ups = ups?.toLong(),
        thumbnail = thumbnail,
        permalink = permalink,
        url = url ?: previewImageUrl,
        preview = preview,
        saved = saved,
        likes = likes,
        selftext = selftext
    )
}

/**
 * Convert a cached UserEntity to a UserAboutListing object
 */
fun UserEntity.toUserAboutListing(): UserAboutListing {
    return UserAboutListing(
        // could be problematic in future but as of now we do not use kind
        kind = "",
        data = UserAbout(
            name = name,
            icon_img = iconUrl,
            snoovatar_img = snoovatarUrl
        )
    )
}

fun SubredditItem.toSubredditEntity(isSubscribed: Boolean): SubredditEntity {
    val d = this.data
    return SubredditEntity(
        id = d.url,
        name = d.displayName,
        title = d.title,
        iconUrl = d.iconUrl,
        subscribers = d.subscribers,
        isNsfw = d.over18 == true,
        isSubscribed = isSubscribed
    )
}