package com.skybound.space.data.paging

data class PagedResult<T>(
    val items: List<T>,
    val hasMore: Boolean,
    val totalCount: Int = -1  // -1 means unknown
)
