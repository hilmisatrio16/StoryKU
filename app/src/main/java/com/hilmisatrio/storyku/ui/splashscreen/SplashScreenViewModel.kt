package com.hilmisatrio.storyku.ui.splashscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hilmisatrio.storyku.data.repository.StoryRepository

class SplashScreenViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun isSessionActive(): LiveData<Boolean> {
        return storyRepository.isActive.asLiveData()
    }

    fun getTheme(): LiveData<Boolean> {
        return storyRepository.themeMode.asLiveData()
    }

}