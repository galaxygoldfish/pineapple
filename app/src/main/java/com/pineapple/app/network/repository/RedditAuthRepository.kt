package com.pineapple.app.network.repository

import com.pineapple.app.consts.MMKVKey
import com.pineapple.app.network.api.RedditApi
import com.pineapple.app.network.api.RedditTokenApi
import com.pineapple.app.network.caching.AppDatabase
import com.tencent.mmkv.MMKV
import okhttp3.Credentials
import javax.inject.Inject
import javax.inject.Singleton

const val USER_AGENT = "android:com.pineapple.app:v1.0-beta (TEST)"

@Singleton
class RedditAuthRepository @Inject constructor(
    private val tokenApi: RedditTokenApi,
    private val mmkv: MMKV,
) {

    private var _accessToken: String? = null
    private var _refreshToken: String? = null
    private var _storedClientId: String? = null
    private var _tokenType = "bearer"

    val accessToken: String? get() = _accessToken
    val clientId: String? get() = _storedClientId

    val isUserless: Boolean get() = mmkv.decodeBool(MMKVKey.USER_GUEST, true)
    val isAuthenticated: Boolean get() = _accessToken != null && !isTokenExpired()

    init {
        loadStoredTokens()
        if (isTokenExpired()) {
            _accessToken = null
            _refreshToken = null
        }
    }

    /**
     * Ensures we have a valid access token in memory/storage.
     * The interceptor will read [accessToken] and prepend the type.
     */
    suspend fun ensureValidToken(clientId: String? = _storedClientId) {
        _storedClientId = clientId ?: _storedClientId
        if (_accessToken.isNullOrBlank() || isTokenExpired()) {
            if (_refreshToken != null && !isUserless) {
                refreshAccessToken()
            } else if (!isUserless || mmkv.decodeString(MMKVKey.API_LOGIN_AUTH_CODE)
                    ?.isNotBlank() == true
            ) {
                authenticateUser()
            } else {
                authenticateUserless()
            }
        }
    }

    /**
     * Get an access token if we have gotten an auth code from Reddit OAuth login flow
     */
    suspend fun authenticateUser() {
        val authCode = mmkv.decodeString(MMKVKey.API_LOGIN_AUTH_CODE)
            ?: throw Exception("No auth code available for user login")
        val response = tokenApi.authenticateUser(
            basicAuth = Credentials.basic(_storedClientId!!, ""),
            authCode = authCode
        )
        if (response.isSuccessful) {
            val auth = response.body()!!
            saveTokens(auth.accessToken, auth.refreshToken, auth.expires, auth.tokenType)
            mmkv.encode(MMKVKey.USER_GUEST, false)
        } else {
            throw Exception("User auth failed: ${response.message()}")
        }
    }

    /**
     * Get an access token without logging in with Reddit
     */
    suspend fun authenticateUserless(
        clientId: String? = _storedClientId,
        testingClientID: Boolean = false
    ) {
        _storedClientId = clientId
        val response = tokenApi.authenticateUserless(
            basicAuth = Credentials.basic(_storedClientId!!, "")
        )
        if (response.isSuccessful) {
            if (!testingClientID) {
                val auth = response.body()!!
                saveTokens(auth.accessToken, auth.refreshToken, auth.expires, auth.tokenType)
            }
        } else {
            throw Exception("Userless auth failed: ${response.message()}")
        }
    }

    /**
     * Using a previously obtained refresh token, get a new access token that is valid
     */
    private suspend fun refreshAccessToken() {
        val response = tokenApi.refreshAccessToken(
            basicAuth = Credentials.basic(_storedClientId!!, ""),
            refreshToken = _refreshToken!!
        )
        if (response.isSuccessful) {
            val auth = response.body()!!
            saveTokens(auth.accessToken, auth.refreshToken, auth.expires, auth.tokenType)
        } else {
            _refreshToken = null
            authenticateUserless()
        }
    }

    /**
     * Update the MMKV table with our most up to date token and authentication information
     */
    private fun saveTokens(
        accessToken: String,
        refreshToken: String?,
        expiresIn: Long,
        tokenType: String?
    ) {
        _accessToken = accessToken
        _refreshToken = refreshToken
        _tokenType = tokenType ?: "bearer"
        mmkv.encode(MMKVKey.ACCESS_TOKEN, accessToken)
        mmkv.encode(MMKVKey.REFRESH_TOKEN, refreshToken)
        mmkv.encode(MMKVKey.TOKEN_EXPIRES, System.currentTimeMillis() + expiresIn * 1000)
        mmkv.encode(MMKVKey.CLIENT_ID, _storedClientId)
        mmkv.encode(MMKVKey.TOKEN_TYPE, _tokenType)
    }

    /**
     * Load any stored tokens from MMKV into memory
     */
    private fun loadStoredTokens() {
        _accessToken = mmkv.decodeString(MMKVKey.ACCESS_TOKEN)
        _refreshToken = mmkv.decodeString(MMKVKey.REFRESH_TOKEN)
        _storedClientId = mmkv.decodeString(MMKVKey.CLIENT_ID)
        _tokenType = mmkv.decodeString(MMKVKey.TOKEN_TYPE, "bearer") ?: "bearer"
    }

    /**
     * Check the time that our access token expires against the current time to determine
     * if we need to request a new one or refresh it
     */
    private fun isTokenExpired(): Boolean {
        return System.currentTimeMillis() > mmkv.decodeLong(MMKVKey.TOKEN_EXPIRES, 0)
    }

    /**
     * Optional helper if the interceptor wants the full header value.
     */
    fun authorizationHeaderOrNull(): String? =
        _accessToken?.let { "$_tokenType $it" }
}