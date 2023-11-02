package com.hilmisatrio.storyku.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hilmisatrio.storyku.data.repository.StoryRepository
import com.hilmisatrio.storyku.data.room.Story
import kotlinx.coroutines.launch

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStories(token: String): LiveData<PagingData<Story>> =
        storyRepository.getAllStories(token).cachedIn(viewModelScope)

    fun getName(): LiveData<String> {
        return storyRepository.nameUser.asLiveData()
    }

    fun isSessionActive(): LiveData<Boolean> {
        return storyRepository.isActive.asLiveData()
    }

    fun getToken(): LiveData<String> {
        return storyRepository.tokenAccess.asLiveData()
    }

    fun getTheme(): LiveData<Boolean> {
        return storyRepository.themeMode.asLiveData()
    }

    fun clearDataSession() {
        viewModelScope.launch {
            storyRepository.clearDataStore()
        }
    }

    fun selectTheme(isDarkMode: Boolean) {
        viewModelScope.launch {
            storyRepository.saveTheme(isDarkMode)
        }
    }
}