package com.skybound.space.data.mapper

interface BaseMapper<in From, out To> {
    fun map(from: From): To
}

fun <From, To> BaseMapper<From, To>.mapList(from: List<From>): List<To> =
    from.map { map(it) }
