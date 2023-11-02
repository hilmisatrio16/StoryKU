package com.hilmisatrio.storyku.data.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.hilmisatrio.storyku.data.remote.retrofit.ApiService
import com.hilmisatrio.storyku.data.room.RemoteKeysStory
import com.hilmisatrio.storyku.data.room.Story
import com.hilmisatrio.storyku.data.room.StoryDatabase

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val token: String
) : RemoteMediator<Int, Story>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Story>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeysStory = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeysStory?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeysStory = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeysStory?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeysStory != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeysStory = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeysStory?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeysStory != null)
                nextKey
            }
        }
        try {
            val response = apiService.getStories(token, page, state.config.pageSize)
            val endOfPaginationReached = response.listStory.isEmpty()
            storyDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    storyDatabase.storyDao().deleteAllStories()
                    storyDatabase.remoteKeysStoryDao().deleteRemoteKeys()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val listStory = response.listStory.map {
                    Story(it.id, it.name, it.description, it.lat, it.lon, it.photoUrl, it.createdAt)
                }

                val keys = response.listStory.map {
                    RemoteKeysStory(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                storyDatabase.storyDao().insertStories(listStory)
                storyDatabase.remoteKeysStoryDao().insertAll(keys)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Story>): RemoteKeysStory? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            storyDatabase.remoteKeysStoryDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Story>): RemoteKeysStory? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            storyDatabase.remoteKeysStoryDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Story>): RemoteKeysStory? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storyDatabase.remoteKeysStoryDao().getRemoteKeysId(id)
            }
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}