package com.dicoding.picodiploma.loginwithanimation.data.paging

import androidx.paging.*
import com.dicoding.picodiploma.loginwithanimation.data.repository.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.service.response.ListStory

class StoriesPagingSource(private val repository: StoryRepository) : PagingSource<Int, ListStory>() {

    companion object {
        private const val INITIAL_PAGE = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStory>): Int? {
        return state.anchorPosition?.let { position ->
            val closestPage = state.closestPageToPosition(position)
            closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStory> {
        return try {
            val currentPage = params.key ?: INITIAL_PAGE
            val stories = repository.fetchPagedStories(null, currentPage, params.loadSize)
            LoadResult.Page(
                data = stories,
                prevKey = if (currentPage == INITIAL_PAGE) null else currentPage - 1,
                nextKey = if (stories.isEmpty()) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
