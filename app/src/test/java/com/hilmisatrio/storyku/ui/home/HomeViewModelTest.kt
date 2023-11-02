package com.hilmisatrio.storyku.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.hilmisatrio.storyku.DataDummyStories
import com.hilmisatrio.storyku.MainDispatcherRule
import com.hilmisatrio.storyku.data.repository.StoryRepository
import com.hilmisatrio.storyku.data.room.Story
import com.hilmisatrio.storyku.getOrAwaitValue
import com.hilmisatrio.storyku.ui.adapter.ListStoriesAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    private val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLVh1WlgyUVVYZEM1SzFlcUoiLCJpYXQiOjE2OTc1Njk5NDF9.-hJDlnmxyeUmOXjytjLLtJ2X_O1NTN9crL4g-ljWHsQ"

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    private lateinit var homeViewModel :HomeViewModel

    @Before
    fun setUp(){
        homeViewModel = HomeViewModel(storyRepository)

    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {

        val dummyStories = DataDummyStories.generateDummyStoryResponse()
        val dataPaging: PagingData<Story> = StoryPagingSource.snapshot(dummyStories)
        val expectedStory = MutableLiveData<PagingData<Story>>()
        expectedStory.value = dataPaging
        Mockito.`when`(storyRepository.getAllStories(token)).thenReturn(expectedStory)
        val actualStory: PagingData<Story> = homeViewModel.getStories(token).getOrAwaitValue()


        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )

        differ.submitData(actualStory)
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val dataPaging: PagingData<Story> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<Story>>()
        expectedStory.value = dataPaging
        Mockito.`when`(storyRepository.getAllStories(token)).thenReturn(expectedStory)
        val actualStory: PagingData<Story> = homeViewModel.getStories(token).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)
        Assert.assertEquals(0, differ.snapshot().size)
    }

}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

class StoryPagingSource : PagingSource<Int, LiveData<List<Story>>>() {
    companion object {
        fun snapshot(itemsStories: List<Story>): PagingData<Story> {
            return PagingData.from(itemsStories)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}