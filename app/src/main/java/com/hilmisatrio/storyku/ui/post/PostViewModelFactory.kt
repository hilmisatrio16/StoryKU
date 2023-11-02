package com.hilmisatrio.storyku.ui.post

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hilmisatrio.storyku.data.repository.StoryRepository
import com.hilmisatrio.storyku.di.Injection

class PostViewModelFactory(private val repository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
                PostViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: PostViewModelFactory? = null
        fun getInstance(context: Context): PostViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: PostViewModelFactory(Injection.provideStoryRepository(context))
            }.also { instance = it }
    }
}