package com.hilmisatrio.storyku.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hilmisatrio.storyku.data.Result
import com.hilmisatrio.storyku.data.remote.request.RequestDataRegister
import com.hilmisatrio.storyku.data.remote.response.ResponseRegister
import com.hilmisatrio.storyku.data.repository.StoryRepository

class RegisterViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun register(dataUser: RequestDataRegister): LiveData<Result<ResponseRegister>> =
        storyRepository.register(dataUser)
}