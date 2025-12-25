package com.pineapple.app.network.interceptor

import com.pineapple.app.network.repository.RedditAuthRepository
import com.pineapple.app.network.repository.RedditRepository
import com.pineapple.app.network.repository.USER_AGENT
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Injects our auth token into all requests, handling token refresh and validity
 * checks as needed so callers do not need to
 */
class AuthInterceptor(private val repository: RedditAuthRepository) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // Skip auth for token endpoint
        if (original.url.host == "www.reddit.com" &&
            original.url.encodedPath.startsWith("/api/v1/access_token")
        ) {
            val tokenReq = original.newBuilder()
                .header("User-Agent", USER_AGENT)
                .build()
            return chain.proceed(tokenReq)
        }

        val authHeader = runBlocking {
            repository.ensureValidToken()
            repository.authorizationHeaderOrNull()
        }
        val newReqBuilder = original.newBuilder()
            .header("User-Agent", USER_AGENT)

        if (!authHeader.isNullOrBlank()) {
            newReqBuilder.header("Authorization", authHeader)
        }

        return chain.proceed(newReqBuilder.build())
    }
}
