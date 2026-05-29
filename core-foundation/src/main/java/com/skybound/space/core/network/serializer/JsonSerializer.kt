package com.skybound.space.core.network.serializer

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object JsonSerializer {
    fun createGson(): Gson = GsonBuilder()
        .setLenient()
        .create()
}
