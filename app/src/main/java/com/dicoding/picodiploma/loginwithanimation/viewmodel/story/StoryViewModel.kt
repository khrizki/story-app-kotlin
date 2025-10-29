package com.dicoding.picodiploma.loginwithanimation.viewmodel.story

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import com.dicoding.picodiploma.loginwithanimation.data.user.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.repository.*
import com.dicoding.picodiploma.loginwithanimation.service.response.ListStory
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class StoryViewModel(
    private val userRepo: UserRepository,
    private val storyRepo: StoryRepository
) : ViewModel() {

    private val _storiesList = MutableLiveData<List<ListStory>>()
    val storiesList: LiveData<List<ListStory>> = _storiesList

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> = _loadingState

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String> = _errorState

    val pagedStories: LiveData<PagingData<ListStory>> =
        storyRepo.getPagedStories().cachedIn(viewModelScope)

    fun fetchMapsStories() {
        _loadingState.value = true

        viewModelScope.launch {
            val session = userRepo.getSession().firstOrNull()
            if (session == null || session.token.isEmpty()) {
                _errorState.value = "User is not logged in or token is missing."
                _loadingState.value = false
                return@launch
            }

            try {
                val response = storyRepo.fetchStories(1)
                response.fold(
                    onSuccess = { body ->
                        _storiesList.value = body.listStory as List<ListStory>
                    },
                    onFailure = { exception ->
                        _errorState.value = "Error: ${exception.message}"
                        Log.d("UserDashboardViewModel", "Error: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _errorState.value = "Unexpected error: ${e.message}"
                Log.d("UserDashboardViewModel", "Error: ${e.message}")
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun getUserSession(): LiveData<UserModel> {
        return userRepo.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            userRepo.logout()
        }
    }
}
