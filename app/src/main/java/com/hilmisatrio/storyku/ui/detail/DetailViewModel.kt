package com.hilmisatrio.storyku.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hilmisatrio.storyku.data.Result
import com.hilmisatrio.storyku.data.remote.response.ResponseDetailStory
import com.hilmisatrio.storyku.data.repository.StoryRepository

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getDetailStory(token: String, id: String): LiveData<Result<ResponseDetailStory>> =
        storyRepository.getDetail(token, id)

    fun getToken(): LiveData<String> {
        return storyRepository.tokenAccess.asLiveData()
    }
}