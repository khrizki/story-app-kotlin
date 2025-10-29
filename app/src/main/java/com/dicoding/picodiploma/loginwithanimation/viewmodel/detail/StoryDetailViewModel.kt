package com.dicoding.picodiploma.loginwithanimation.viewmodel.detail

import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.data.repository.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.service.response.DetailStoryResponse
import kotlinx.coroutines.launch

class StoryDetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _storyDetailResult = MutableLiveData<DetailStoryResponse?>()
    val storyDetail: LiveData<DetailStoryResponse?> = _storyDetailResult

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> = _loadingState

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String> = _errorState

    fun fetchStoryDetail(storyId: String) {
        _loadingState.value = true

        viewModelScope.launch {
            try {
                val response = storyRepository.fetchStoryDetails(storyId)
                if (response.isSuccess) {
                    response.getOrNull()?.let {
                        _storyDetailResult.value = it
                    } ?: run {
                        _errorState.value = "No data available in the response"
                    }
                } else {
                    _errorState.value = "Request failed: ${response.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _errorState.value = "Error occurred: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }
}
