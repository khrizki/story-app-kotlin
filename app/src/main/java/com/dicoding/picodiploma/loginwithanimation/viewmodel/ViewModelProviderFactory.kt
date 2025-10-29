package com.dicoding.picodiploma.loginwithanimation.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.data.repository.*
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.viewmodel.addStory.AddStoryViewModel
import com.dicoding.picodiploma.loginwithanimation.viewmodel.detail.StoryDetailViewModel
import com.dicoding.picodiploma.loginwithanimation.viewmodel.login.LoginViewModel
import com.dicoding.picodiploma.loginwithanimation.viewmodel.story.StoryViewModel
import com.dicoding.picodiploma.loginwithanimation.viewmodel.signup.SignupViewModel

class ViewModelProviderFactory(private val userRepo: UserRepository, private val storyRepo: StoryRepository) : ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var INSTANCE: ViewModelProviderFactory? = null

        @JvmStatic
        fun getInstance(appContext: Context): ViewModelProviderFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelProviderFactory(
                    Injection.provideUserRepository(appContext),
                    Injection.provideStoryRepository(appContext)
                ).also { INSTANCE = it }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(userRepo, storyRepo) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(storyRepo, userRepo) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepo) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(userRepo) as T
            }
            modelClass.isAssignableFrom(StoryDetailViewModel::class.java) -> {
                StoryDetailViewModel(storyRepo) as T
            }
            else -> throw IllegalArgumentException("ViewModel class is not recognized: ${modelClass.name}")
        }
    }
}