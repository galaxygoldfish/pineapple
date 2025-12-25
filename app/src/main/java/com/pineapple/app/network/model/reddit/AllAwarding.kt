package com.pineapple.app.network.model.reddit

import com.google.gson.annotations.SerializedName

data class AllAwarding(
    @SerializedName("giver_coin_reward")
    val giverCoinReward: Long? = null,

    @SerializedName("subreddit_id")
    val subredditID: Any? = null,

    @SerializedName("is_new")
    val isNew: Boolean,

    @SerializedName("days_of_drip_extension")
    val daysOfDripExtension: Long,

    @SerializedName("coin_price")
    val coinPrice: Long,

    val id: String,

    @SerializedName("penny_donate")
    val pennyDonate: Long? = null,

    @SerializedName("award_sub_type")
    val awardSubType: String,

    @SerializedName("coin_reward")
    val coinReward: Long,

    @SerializedName("icon_url")
    val iconURL: String,

    @SerializedName("days_of_premium")
    val daysOfPremium: Long,

    @SerializedName("tiers_by_required_awardings")
    val tiersByRequiredAwardings: Any? = null,

    @SerializedName("resized_icons")
    val resizedIcons: List<ResizedIcon>,

    @SerializedName("icon_width")
    val iconWidth: Long,

    @SerializedName("static_icon_width")
    val staticIconWidth: Long,

    @SerializedName("start_date")
    val startDate: Any? = null,

    @SerializedName("is_enabled")
    val isEnabled: Boolean,

    @SerializedName("awardings_required_to_grant_benefits")
    val awardingsRequiredToGrantBenefits: Any? = null,

    val description: String,

    @SerializedName("end_date")
    val endDate: Any? = null,

    @SerializedName("subreddit_coin_reward")
    val subredditCoinReward: Long,

    val count: Long,

    @SerializedName("static_icon_height")
    val staticIconHeight: Long,

    val name: String,

    @SerializedName("resized_static_icons")
    val resizedStaticIcons: List<ResizedIcon>,

    @SerializedName("icon_format")
    val iconFormat: String? = null,

    @SerializedName("icon_height")
    val iconHeight: Long,

    @SerializedName("penny_price")
    val pennyPrice: Long? = null,

    @SerializedName("award_type")
    val awardType: String,

    @SerializedName("static_icon_url")
    val staticIconURL: String
)
