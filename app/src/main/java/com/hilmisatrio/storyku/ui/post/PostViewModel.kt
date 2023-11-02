package com.hilmisatrio.storyku.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hilmisatrio.storyku.data.remote.request.RequestNewStory
import com.hilmisatrio.storyku.data.repository.StoryRepository

class PostViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private var _latitute: MutableLiveData<Double> = MutableLiveData()
    val latitute: LiveData<Double> get() = _latitute

    private var _longitude: MutableLiveData<Double> = MutableLiveData()
    val longitude: LiveData<Double> get() = _longitude

    fun uploadStory(token: String, dataUpload: RequestNewStory) = storyRepository.uploadNewStory(token, dataUpload)

    fun getToken(): LiveData<String> {
        return storyRepository.tokenAccess.asLiveData()
    }

    fun setLocation(lat: Double, lon: Double){
        _latitute.postValue(lat)
        _longitude.postValue(lon)
    }
}