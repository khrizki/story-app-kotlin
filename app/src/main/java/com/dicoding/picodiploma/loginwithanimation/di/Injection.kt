package com.dicoding.picodiploma.loginwithanimation.di

import android.content.Context
import com.dicoding.picodiploma.loginwithanimation.data.user.*
import com.dicoding.picodiploma.loginwithanimation.data.repository.*
import com.dicoding.picodiploma.loginwithanimation.service.api.*

object Injection {

    fun provideUserRepository(
        appContext: Context,
        apiClient: ApiService = ApiConfig.getApiService()
    ): UserRepository {
        val sessionManager = SessionManager.getInstance(appContext.userDataStore)
        return UserRepository.getInstance(sessionManager, apiClient)
    }

    fun provideStoryRepository(appContext: Context): StoryRepository {
        val sessionManager = SessionManager.getInstance(appContext.userDataStore)
        val apiClient = ApiConfig.getApiService()
        return StoryRepository.createInstance(apiClient, sessionManager)
    }
}
