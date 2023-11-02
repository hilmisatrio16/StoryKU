package com.hilmisatrio.storyku.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import com.hilmisatrio.storyku.data.dsprefs.AuthPreferences
import com.hilmisatrio.storyku.data.remote.response.ResponseStories
import com.hilmisatrio.storyku.data.remote.retrofit.ApiService
import com.hilmisatrio.storyku.data.Result
import com.hilmisatrio.storyku.data.dsprefs.SettingPreferences
import com.hilmisatrio.storyku.data.remote.response.ErrorResponse
import com.hilmisatrio.storyku.data.remote.request.RequestDataRegister
import com.hilmisatrio.storyku.data.remote.request.RequestNewStory
import com.hilmisatrio.storyku.data.remote.response.ResponseDetailStory
import com.hilmisatrio.storyku.data.remote.response.ResponseLogin
import com.hilmisatrio.storyku.data.remote.response.ResponseNewStory
import com.hilmisatrio.storyku.data.remote.response.ResponseRegister
import com.hilmisatrio.storyku.data.remotemediator.StoryRemoteMediator
import com.hilmisatrio.storyku.data.room.StoryDatabase
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val authPrefs: AuthPreferences,
    private val settingPrefs: SettingPreferences,
    private val storyDatabase: StoryDatabase
) {

    fun getAllStories(token: String): LiveData<PagingData<com.hilmisatrio.storyku.data.room.Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 8
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, "Bearer $token"),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData

    }

    fun getStoriesFromLocation(token: String): LiveData<Result<ResponseStories>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesFromLocation("Bearer $token")
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBodyResponse = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessageResponse = errorBodyResponse.message
            emit(Result.Error(errorMessageResponse.toString()))
        }
    }

    fun login(email: String, password: String): LiveData<Result<ResponseLogin>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.loginUser(email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBodyResponse = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessageResponse = errorBodyResponse.message
            emit(Result.Error(errorMessageResponse.toString()))
        }
    }

    fun register(dataRegist: RequestDataRegister): LiveData<Result<ResponseRegister>> = liveData {
        emit(Result.Loading)
        try {
            val response =
                apiService.registerUser(dataRegist.nama, dataRegist.email, dataRegist.password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBodyResponse = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessageResponse = errorBodyResponse.message
            emit(Result.Error(errorMessageResponse.toString()))
        }
    }

    fun getDetail(token: String, id: String): LiveData<Result<ResponseDetailStory>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailStory("Bearer $token", id)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBodyResponse = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessageResponse = errorBodyResponse.message
            emit(Result.Error(errorMessageResponse.toString()))
        }
    }

    fun uploadNewStory(
        token: String,
        dataUpload: RequestNewStory
    ): LiveData<Result<ResponseNewStory>> = liveData {
        emit(Result.Loading)
        val requestDescription = dataUpload.description.toRequestBody("text/plain".toMediaType())
        val requestLatitute = dataUpload.lat.toString().toRequestBody("text/plain".toMediaType())
        val requestLongitute = dataUpload.lon.toString().toRequestBody("text/plain".toMediaType())
        val requestImageStoryFile =
            dataUpload.imageStoryFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            dataUpload.imageStoryFile.name,
            requestImageStoryFile
        )
        try {
            if (dataUpload.lat != null && dataUpload.lon != null) {
                val response =
                    apiService.uploadStoryWithLocation(
                        "Bearer $token",
                        multipartBody,
                        requestDescription,
                        requestLatitute,
                        requestLongitute
                    )
                emit(Result.Success(response))
            } else {
                val response =
                    apiService.uploadStoryWithoutLocation(
                        "Bearer $token",
                        multipartBody,
                        requestDescription
                    )
                emit(Result.Success(response))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBodyResponse = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessageResponse = errorBodyResponse.message
            emit(Result.Error(errorMessageResponse.toString()))
        }
    }

    suspend fun clearDataStore() {
        authPrefs.clearData()
    }

    suspend fun isSessionActive(token: String, name: String) {
        authPrefs.saveDataAuth(true, token, name)
    }

    suspend fun saveTheme(isDarkMode: Boolean) {
        settingPrefs.saveTheme(isDarkMode)
    }

    val isActive: Flow<Boolean> = authPrefs.getSessionLogin()

    val nameUser: Flow<String> = authPrefs.getName()

    val tokenAccess: Flow<String> = authPrefs.getToken()

    val themeMode: Flow<Boolean> = settingPrefs.getTheme()

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            authPrefs: AuthPreferences,
            settingPrefs: SettingPreferences,
            storyDatabase: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, authPrefs, settingPrefs, storyDatabase)
            }.also { instance = it }
    }
}