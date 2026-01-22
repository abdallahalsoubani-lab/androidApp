package com.template.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that adds common headers to all requests.
 */
class HeadersInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val requestWithHeaders = originalRequest.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("User-Agent", "AndroidTemplate/1.0")
            .build()

        return chain.proceed(requestWithHeaders)
    }
}
