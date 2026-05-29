package com.skybound.space.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CancellationException

abstract class BasePagingSource<T : Any> : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 1
        return try {
            val result = fetch(page, params.loadSize)
            LoadResult.Page(
                data = result.items,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (result.hasMore) page + 1 else null
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    protected abstract suspend fun fetch(page: Int, pageSize: Int): PagedResult<T>
}
