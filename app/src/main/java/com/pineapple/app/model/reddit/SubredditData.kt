package com.pineapple.app.model.reddit

import com.google.gson.annotations.SerializedName

data class SubredditData(
    val title: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("display_name_prefixed")
    val displayNamePrefixed: String,
    @SerializedName("description_html")
    val descriptionHtml: String,
    val description: String,
    val created: Long,
    val over18: Boolean,
    val url: String,
    @SerializedName("community_icon")
    val iconUrl: String,
    val subscribers: Long,
    val public_description: String
)
