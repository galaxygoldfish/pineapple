package com.pineapple.app.network.model.reddit

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

typealias MediaEmbed = JsonObject
data class PostData(

    @SerializedName("approved_at_utc")
    val approvedAtUTC: Any? = null,

    val subreddit: String? = null,
    val selftext: String? = null,

    @SerializedName("author_fullname")
    val authorFullname: String? = null,

    val saved: Boolean? = null,

    @SerializedName("mod_reason_title")
    val modReasonTitle: Any? = null,

    val gilded: Long? = null,
    val clicked: Boolean? = null,
    val title: String? = null,

    @SerializedName("link_flair_richtext")
    val linkFlairRichtext: List<FlairRichItem>? = null,

    @SerializedName("subreddit_name_prefixed")
    val subredditNamePrefixed: String? = null,

    val hidden: Boolean? = null,
    val pwls: Long? = null,

    @SerializedName("link_flair_css_class")
    val linkFlairCSSClass: String? = null,

    val downs: Long? = null,

    @SerializedName("thumbnail_height")
    val thumbnailHeight: Long? = null,

    @SerializedName("top_awarded_type")
    val topAwardedType: Any? = null,

    @SerializedName("hide_score")
    val hideScore: Boolean? = null,

    val name: String? = null,
    val quarantine: Boolean? = null,

    @SerializedName("link_flair_text_color")
    val linkFlairTextColor: String? = null,

    @SerializedName("upvote_ratio")
    val upvoteRatio: Double? = null,

    @SerializedName("author_flair_background_color")
    val authorFlairBackgroundColor: Any? = null,

    @SerializedName("subreddit_type")
    val subredditType: String? = null,

    val ups: Long? = null,

    @SerializedName("total_awards_received")
    val totalAwardsReceived: Long? = null,

    @SerializedName("media_embed")
    val mediaEmbed: MediaEmbed? = null,

    @SerializedName("thumbnail_width")
    val thumbnailWidth: Long? = null,

    @SerializedName("author_flair_template_id")
    val authorFlairTemplateID: Any? = null,

    @SerializedName("is_original_content")
    val isOriginalContent: Boolean? = null,

    @SerializedName("user_reports")
    val userReports: List<Any?>? = null,

    @SerializedName("secure_media")
    val secureMedia: SecureMedia? = null,

    @SerializedName("is_reddit_media_domain")
    val isRedditMediaDomain: Boolean? = null,

    @SerializedName("is_meta")
    val isMeta: Boolean? = null,

    val category: Any? = null,

    @SerializedName("secure_media_embed")
    val secureMediaEmbed: MediaEmbed? = null,

    @SerializedName("link_flair_text")
    val linkFlairText: String? = null,

    @SerializedName("can_mod_post")
    val canModPost: Boolean? = null,

    val score: Long? = null,

    @SerializedName("approved_by")
    val approvedBy: Any? = null,

    @SerializedName("is_created_from_ads_ui")
    val isCreatedFromAdsUI: Boolean? = null,

    @SerializedName("author_premium")
    val authorPremium: Boolean? = null,

    val thumbnail: String? = null,
    val edited: Any? = null,

    @SerializedName("author_flair_css_class")
    val authorFlairCSSClass: Any? = null,

    @SerializedName("author_flair_richtext")
    val authorFlairRichtext: List<Any?>? = null,

    val gildings: Gildings? = null,

    @SerializedName("post_hint")
    val postHint: String? = null,

    @SerializedName("content_categories")
    val contentCategories: Any? = null,

    @SerializedName("is_self")
    val isSelf: Boolean? = null,

    @SerializedName("mod_note")
    val modNote: Any? = null,

    val created: Long? = null,

    @SerializedName("link_flair_type")
    val linkFlairType: String? = null,

    val wls: Long? = null,

    @SerializedName("removed_by_category")
    val removedByCategory: Any? = null,

    @SerializedName("banned_by")
    val bannedBy: Any? = null,

    @SerializedName("author_flair_type")
    val authorFlairType: String? = null,

    val domain: String? = null,

    @SerializedName("allow_live_comments")
    val allowLiveComments: Boolean? = null,

    @SerializedName("selftext_html")
    val selftextHTML: Any? = null,

    val likes: Boolean? = null,

    @SerializedName("suggested_sort")
    val suggestedSort: Any? = null,

    @SerializedName("banned_at_utc")
    val bannedAtUTC: Any? = null,

    @SerializedName("url_overridden_by_dest")
    val urlOverriddenByDest: String? = null,

    @SerializedName("view_count")
    val viewCount: Any? = null,

    val archived: Boolean? = null,

    @SerializedName("no_follow")
    val noFollow: Boolean? = null,

    @SerializedName("is_crosspostable")
    val isCrosspostable: Boolean? = null,

    val pinned: Boolean? = null,

    @SerializedName("over_18")
    val over18: Boolean? = null,

    val preview: Preview? = null,

    @SerializedName("all_awardings")
    val allAwardings: List<AllAwarding>? = null,

    val awarders: List<Any?>? = null,

    @SerializedName("media_only")
    val mediaOnly: Boolean? = null,

    @SerializedName("can_gild")
    val canGild: Boolean? = null,

    val spoiler: Boolean? = null,
    val locked: Boolean? = null,

    @SerializedName("author_flair_text")
    val authorFlairText: Any? = null,

    @SerializedName("treatment_tags")
    val treatmentTags: List<Any?>? = null,

    val visited: Boolean? = null,

    @SerializedName("removed_by")
    val removedBy: Any? = null,

    @SerializedName("num_reports")
    val numReports: Any? = null,

    val distinguished: Any? = null,

    @SerializedName("subreddit_id")
    val subredditID: String? = null,

    @SerializedName("author_is_blocked")
    val authorIsBlocked: Boolean? = null,

    @SerializedName("mod_reason_by")
    val modReasonBy: Any? = null,

    @SerializedName("removal_reason")
    val removalReason: Any? = null,

    @SerializedName("link_flair_background_color")
    val linkFlairBackgroundColor: String? = null,

    val id: String? = null,

    @SerializedName("is_robot_indexable")
    val isRobotIndexable: Boolean? = null,

    @SerializedName("report_reasons")
    val reportReasons: Any? = null,

    val author: String? = null,

    @SerializedName("discussion_type")
    val discussionType: Any? = null,

    @SerializedName("num_comments")
    val numComments: Long? = null,

    @SerializedName("send_replies")
    val sendReplies: Boolean? = null,

    @SerializedName("whitelist_status")
    val whitelistStatus: String? = null,

    @SerializedName("contest_mode")
    val contestMode: Boolean? = null,

    @SerializedName("mod_reports")
    val modReports: List<Any?>? = null,

    @SerializedName("author_patreon_flair")
    val authorPatreonFlair: Boolean? = null,

    @SerializedName("author_flair_text_color")
    val authorFlairTextColor: Any? = null,

    val permalink: String? = null,

    @SerializedName("parent_whitelist_status")
    val parentWhitelistStatus: String? = null,

    val stickied: Boolean? = null,
    val url: String? = null,

    @SerializedName("subreddit_subscribers")
    val subredditSubscribers: Long? = null,

    @SerializedName("created_utc")
    val createdUTC: Long? = null,

    @SerializedName("num_crossposts")
    val numCrossposts: Long? = null,

    val media: Any? = null,

    @SerializedName("is_video")
    val isVideo: Boolean? = null
)
