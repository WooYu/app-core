package com.skybound.space.data.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

fun <T : Any> createPager(
    pageSize: Int = 20,
    prefetchDistance: Int = pageSize,
    enablePlaceholders: Boolean = false,
    sourceFactory: () -> BasePagingSource<T>
): Flow<PagingData<T>> = Pager(
    config = PagingConfig(
        pageSize = pageSize,
        prefetchDistance = prefetchDistance,
        enablePlaceholders = enablePlaceholders
    ),
    pagingSourceFactory = sourceFactory
).flow
