package com.hilmisatrio.storyku.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hilmisatrio.storyku.data.Result
import com.hilmisatrio.storyku.data.remote.response.ResponseLogin
import com.hilmisatrio.storyku.data.repository.StoryRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun login(email: String, password: String): LiveData<Result<ResponseLogin>> =
        storyRepository.login(email, password)

    fun sessionActive(token: String, name: String) {
        viewModelScope.launch {
            storyRepository.isSessionActive(token, name)
        }
    }

    fun getToken(): LiveData<String> {
        return storyRepository.tokenAccess.asLiveData()
    }
}