package com.pineapple.app.network.api

import com.pineapple.app.network.model.auth.AuthResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import javax.inject.Qualifier

/**
 * Interface with the Reddit OAuth API used to authenticate, request, and refresh tokens
 * that are then passed to all calls made with the RedditApi interface
 */
interface RedditTokenApi {

    /**
     * Request an access token for the API without a user context
     * @param basicAuth The basic authentication header containing the client ID as username and an empty password
     * @param grantType The type of grant being requested, leave as installed client
     * @param deviceID A unique device identifier, or leave as default to avoid tracking
     * @return A Response object containing the AuthResponse with access token details
     */
    @FormUrlEncoded
    @POST("api/v1/access_token")
    suspend fun authenticateUserless(
        @Header("Authorization") basicAuth: String,
        @Field("grant_type") grantType: String = "https://oauth.reddit.com/grants/installed_client",
        @Field("device_id") deviceID: String = "DO_NOT_TRACK_THIS_DEVICE"
    ): Response<AuthResponse>

    /**
     * Request a new access token if you already have one that expired, and a refresh token
     * @param basicAuth The basic authentication header containing the client ID as username and an empty password
     * @param grantType The type of grant being requested, leave as refresh token
     * @param refreshToken The refresh token previously obtained during authentication
     * @return A Response object containing the AuthResponse with new access and refresh token details
     */
    @FormUrlEncoded
    @POST("api/v1/access_token")
    suspend fun refreshAccessToken(
        @Header("Authorization") basicAuth: String,
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String
    ): Response<AuthResponse>

    /**
     * Request an access token after having authenticated using OAuth in the browser, so that
     * future API calls can be made on behalf of the authenticated user
     * @param basicAuth The basic authentication header containing the client ID as username and an empty password
     * @param grantType The type of grant being requested, leave as authorization code
     * @param authCode The authorization code received from the OAuth redirect after user login
     * @param redirectURI The redirect URI used during the OAuth authentication
     * @return A Response object containing the AuthResponse with access token and refresh token details
     */
    @FormUrlEncoded
    @POST("/api/v1/access_token")
    suspend fun authenticateUser(
        @Header("Authorization") basicAuth: String ,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") authCode: String,
        @Field("redirect_uri") redirectURI: String = "pineapple://login"
    ) : Response<AuthResponse>

}

/**
 * Qualifier annotation to identify the Retrofit instance for RedditTokenApi
 * (and differentiate it between [RedditApi] in dependency injection)
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class TokenRetrofit