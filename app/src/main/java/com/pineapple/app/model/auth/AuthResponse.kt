package com.pineapple.app.model.auth

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("access_token")
    var accessToken: String,
    @SerializedName("token_type")
    var tokenType: String,
    @SerializedName("expires_in")
    var expires: Long,
    @SerializedName("scope")
    var scope: String,
    @SerializedName("refresh_token")
    var refreshToken: String? = null
)
