package com.pineapple.app.network.interceptor

import com.pineapple.app.network.repository.USER_AGENT
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Injects the User-Agent header into each request
 */
class TokenUserAgentInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request().newBuilder()
            .header("User-Agent", USER_AGENT)
            .build()
        return chain.proceed(req)
    }
}