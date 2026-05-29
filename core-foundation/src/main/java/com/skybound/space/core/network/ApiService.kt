package com.skybound.space.core.network

import retrofit2.Retrofit

interface ApiService

inline fun <reified T : ApiService> Retrofit.createService(): T = create(T::class.java)
