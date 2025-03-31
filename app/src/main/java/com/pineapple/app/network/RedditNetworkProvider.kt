package com.pineapple.app.network

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.pineapple.app.BuildConfig
import com.pineapple.app.model.auth.AuthResponse
import com.pineapple.app.model.reddit.*
import com.pineapple.app.network.NetworkServiceBuilder.OAUTH_BASE_URL
import com.pineapple.app.network.NetworkServiceBuilder.REDDIT_BASE_URL
import com.pineapple.app.network.NetworkServiceBuilder.apiService
import com.pineapple.app.network.NetworkServiceBuilder.rawApiService
import com.pineapple.app.util.getPreferences
import okhttp3.Credentials

class RedditNetworkProvider(context: Context) {

    private val redditNetworkService: RedditNetworkService by lazy {
        apiService(OAUTH_BASE_URL)
    }
    private val rawRedditNetworkService: RedditNetworkService by lazy {
        rawApiService(OAUTH_BASE_URL)
    }
    private val authNetworkService: RedditNetworkService by lazy {
        apiService(REDDIT_BASE_URL)
    }
    private val sharedPreferences = context.let {
        it.getSharedPreferences(it.packageName, MODE_PRIVATE)
    }
    private val USER_AGENT = "android:com.pineapple.app:${BuildConfig.VERSION_NAME} (TEST)"
    private val credUserName = context.getPreferences().getString("CLIENT_SECRET", "").orEmpty()

    private suspend fun tokenVerity(): String {
        val currentUnixTime = System.currentTimeMillis()
        val tokenExpireEpoch = sharedPreferences.getLong("API_ACCESS_TOKEN_EXPIRE", 0)

        fun processAuthResponse(authResponse: AuthResponse) {
            sharedPreferences.edit().apply {
                putString("API_ACCESS_TOKEN", authResponse.accessToken)
                putLong("API_ACCESS_TOKEN_EXPIRE", System.currentTimeMillis() + authResponse.expires)
                putString("API_ACCESS_TOKEN_TYPE", authResponse.tokenType)
                putString("API_ACCESS_TOKEN_REFRESH_TOKEN", authResponse.refreshToken)
                commit()
            }
        }

        suspend fun authUserless() {
            Log.e("TAG", "authenticating userless")
            processAuthResponse(
                authNetworkService.authenticateUserless(
                    Credentials.basic(username = credUserName, password = "")
                )
            )
        }

        if (sharedPreferences.getString("API_ACCESS_TOKEN", "")!!.isBlank()) {
            if (sharedPreferences.getBoolean("USER_GUEST", true)) {
                authUserless()
            } else {
                Log.e("TAG", "authenticating user")
                processAuthResponse(
                    authNetworkService.authenticateUser(
                        Credentials.basic(username = credUserName, password = ""),
                        authCode = sharedPreferences.getString("API_LOGIN_AUTH_CODE", "").toString()
                    )
                )
            }
        } else if (currentUnixTime >= tokenExpireEpoch) {
            val refreshToken = sharedPreferences.getString("API_ACCESS_TOKEN_REFRESH_TOKEN", "").toString()
            if (sharedPreferences.getBoolean("USER_GUEST", true)) {
                authUserless()
            } else if (refreshToken.isNotBlank()) {
                Log.e("TAG", "refreshing token user")
                processAuthResponse(
                    authNetworkService.refreshAuthToken(
                        Credentials.basic(username = credUserName, password = ""),
                        refreshToken = refreshToken
                    )
                )
            } else {
                processAuthResponse(
                    authNetworkService.authenticateUser(
                        Credentials.basic(username = credUserName, password = ""),
                        authCode = sharedPreferences.getString("API_LOGIN_AUTH_CODE", "").toString()
                    )
                )
            }
        }
        return sharedPreferences.let {
            "${it.getString("API_ACCESS_TOKEN_TYPE", "bearer")!!} ${it.getString("API_ACCESS_TOKEN", "")!!}"
        }
    }

    suspend fun fetchSubreddit(name: String, sort: String, time: String, after: String? = null): PostListing {
        return redditNetworkService.fetchSubreddit(tokenVerity(), USER_AGENT, name, sort, time, after)
    }

    suspend fun fetchPost(name: String, id: String, post: String) : String {
        return rawRedditNetworkService.fetchPost(tokenVerity(), USER_AGENT, name, id, post)
    }

    suspend fun fetchTopSubreddits() : Listing<SubredditItem> {
        return redditNetworkService.fetchTopSubreddits(tokenVerity(), USER_AGENT)
    }

    suspend fun fetchTopUsers() : Listing<CondensedUserAboutListing> {
        return redditNetworkService.fetchTopUsers(tokenVerity(), USER_AGENT)
    }

    suspend fun fetchSubredditInfo(subreddit: String) : SubredditInfo {
        return redditNetworkService.fetchSubredditInfo(tokenVerity(), USER_AGENT, subreddit)
    }

    suspend fun fetchUserInfo(user: String) : UserAboutListing {
        return redditNetworkService.fetchUserInfo(tokenVerity(), USER_AGENT, user)
    }

    suspend fun searchPosts(query: String) : Listing<PostItem> {
        return redditNetworkService.searchPosts(tokenVerity(), USER_AGENT, query)
    }

    suspend fun searchCommunities(query: String) : Listing<SubredditItem> {
        return redditNetworkService.searchCommunities(tokenVerity(), USER_AGENT, query)
    }

    suspend fun searchUsers(query: String) : ListingBase<UserAboutListing> {
        return redditNetworkService.searchUsers(tokenVerity(), USER_AGENT, query)
    }

    suspend fun getUserPosts(user: String) : ListingBase<PostItem> {
        return redditNetworkService.getUserPosts(tokenVerity(), USER_AGENT, user)
    }

    suspend fun getUserComments(user: String) : ListingBase<CommentPreDataNull> {
        return redditNetworkService.getUserComments(tokenVerity(), USER_AGENT, user)
    }

    suspend fun fetchAccountInfo() : AboutAccount {
        return redditNetworkService.getCurrentAccountInfo(tokenVerity(), USER_AGENT)
    }

}