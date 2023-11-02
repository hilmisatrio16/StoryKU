package com.hilmisatrio.storyku.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hilmisatrio.storyku.data.repository.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStoriesFromLocation(token: String) = storyRepository.getStoriesFromLocation(token)

    fun getToken(): LiveData<String> {
        return storyRepository.tokenAccess.asLiveData()
    }
}