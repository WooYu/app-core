package com.skybound.space.data.paging

import androidx.paging.PagingSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BasePagingSourceTest {

    private val fakeData = (1..25).map { "item$it" }

    private val pagingSource = object : BasePagingSource<String>() {
        override suspend fun fetch(page: Int, pageSize: Int): PagedResult<String> {
            val start = (page - 1) * pageSize
            val end = minOf(start + pageSize, fakeData.size)
            return if (start >= fakeData.size) {
                PagedResult(emptyList(), hasMore = false)
            } else {
                PagedResult(
                    items = fakeData.subList(start, end),
                    hasMore = end < fakeData.size
                )
            }
        }
    }

    @Test
    fun `first page load returns correct items`() = runTest {
        val params = PagingSource.LoadParams.Refresh<Int>(
            key = null,
            loadSize = 10,
            placeholdersEnabled = false
        )
        val result = pagingSource.load(params) as PagingSource.LoadResult.Page
        assertEquals(10, result.data.size)
        assertEquals("item1", result.data.first())
        assertNull(result.prevKey)
        assertEquals(2, result.nextKey)
    }

    @Test
    fun `last page has no nextKey`() = runTest {
        val params = PagingSource.LoadParams.Refresh(
            key = 3,
            loadSize = 10,
            placeholdersEnabled = false
        )
        val result = pagingSource.load(params) as PagingSource.LoadResult.Page
        assertEquals(5, result.data.size) // items 21-25
        assertNull(result.nextKey)
    }

    @Test
    fun `error during fetch returns LoadResult Error`() = runTest {
        val failingSource = object : BasePagingSource<String>() {
            override suspend fun fetch(page: Int, pageSize: Int): PagedResult<String> {
                throw RuntimeException("network error")
            }
        }
        val params = PagingSource.LoadParams.Refresh<Int>(
            key = null, loadSize = 10, placeholdersEnabled = false
        )
        assertTrue(failingSource.load(params) is PagingSource.LoadResult.Error)
    }
}
