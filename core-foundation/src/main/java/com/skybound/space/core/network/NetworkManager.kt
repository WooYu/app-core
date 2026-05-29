package com.skybound.space.core.network

import com.skybound.space.core.network.interceptor.CommonHeaderInterceptor
import com.skybound.space.core.network.serializer.JsonSerializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class NetworkManager(private val config: NetworkConfig) {

    private val okHttpClient: OkHttpClient by lazy { buildOkHttpClient() }
    private val retrofit: Retrofit by lazy { buildRetrofit() }

    private fun buildOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(config.readTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeoutSeconds, TimeUnit.SECONDS)
            .apply {
                if (config.enableLogging) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                }
            }
            .build()
    }

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(JsonSerializer.createGson()))
            .build()
    }

    open fun <T> createApi(clazz: Class<T>): T = retrofit.create(clazz)

    fun withAuth(tokenProvider: () -> String?): NetworkManager {
        val authClient = okHttpClient.newBuilder()
            .addInterceptor(com.skybound.space.core.network.auth.AuthInterceptor(tokenProvider))
            .build()
        val authRetrofit = retrofit.newBuilder().client(authClient).build()
        return NetworkManagerWithClient(config, authRetrofit)
    }

    private class NetworkManagerWithClient(
        config: NetworkConfig,
        private val customRetrofit: Retrofit
    ) : NetworkManager(config) {
        override fun <T> createApi(clazz: Class<T>): T = customRetrofit.create(clazz)
    }
}
