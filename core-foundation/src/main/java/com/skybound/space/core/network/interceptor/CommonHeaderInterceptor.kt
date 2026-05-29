package com.skybound.space.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class CommonHeaderInterceptor(
    private val headersProvider: () -> Map<String, String>
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        headersProvider().forEach { (key, value) -> builder.addHeader(key, value) }
        return chain.proceed(builder.build())
    }
}
