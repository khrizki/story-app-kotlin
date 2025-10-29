package com.dicoding.picodiploma.loginwithanimation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.picodiploma.loginwithanimation.*
import com.dicoding.picodiploma.loginwithanimation.data.repository.*
import com.dicoding.picodiploma.loginwithanimation.awaitValue
import com.dicoding.picodiploma.loginwithanimation.service.response.ListStory
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryPagingAdapter
import com.dicoding.picodiploma.loginwithanimation.viewmodel.story.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `when Get Quote Should Not Null and Return Data`() = runTest {
        val sampleStories = DataDummy.createSampleStories()
        val pagingData: PagingData<ListStory> = StoryPagingSource.createSnapshot(sampleStories)
        val liveData = MutableLiveData<PagingData<ListStory>>()
        liveData.value = pagingData
        Mockito.`when`(storyRepository.getPagedStories()).thenReturn(liveData)

        val viewModel = StoryViewModel(userRepository, storyRepository)
        val resultData: PagingData<ListStory> = viewModel.pagedStories.awaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryPagingAdapter.STORY_DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(resultData)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(sampleStories.size, differ.snapshot().size)
        Assert.assertEquals(sampleStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Quote Empty Should Return No Data`() = runTest {
        val emptyData: PagingData<ListStory> = PagingData.from(emptyList())
        val liveData = MutableLiveData<PagingData<ListStory>>()
        liveData.value = emptyData
        Mockito.`when`(storyRepository.getPagedStories()).thenReturn(liveData)

        val viewModel = StoryViewModel(userRepository, storyRepository)
        val resultData: PagingData<ListStory> = viewModel.pagedStories.awaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryPagingAdapter.STORY_DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(resultData)
        Assert.assertEquals(0, differ.snapshot().size) // Check that the snapshot is empty
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

class StoryPagingSource : PagingSource<Int, LiveData<List<ListStory>>>() {
    companion object {
        fun createSnapshot(items: List<ListStory>): PagingData<ListStory> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStory>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStory>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}
