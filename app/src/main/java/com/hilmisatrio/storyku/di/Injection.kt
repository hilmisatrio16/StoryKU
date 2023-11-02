package com.hilmisatrio.storyku.di

import android.content.Context
import com.hilmisatrio.storyku.data.dsprefs.AuthPreferences
import com.hilmisatrio.storyku.data.dsprefs.SettingPreferences
import com.hilmisatrio.storyku.data.dsprefs.dataStore
import com.hilmisatrio.storyku.data.remote.retrofit.ApiConfig
import com.hilmisatrio.storyku.data.repository.StoryRepository
import com.hilmisatrio.storyku.data.room.StoryDatabase

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val authPref = AuthPreferences.getInstance(context.dataStore)
        val settingPref = SettingPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        val storyDatabase = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiService, authPref, settingPref, storyDatabase)
    }
}