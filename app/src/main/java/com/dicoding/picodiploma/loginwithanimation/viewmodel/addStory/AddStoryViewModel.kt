package com.dicoding.picodiploma.loginwithanimation.viewmodel.addStory

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.data.repository.*
import com.dicoding.picodiploma.loginwithanimation.service.response.AddStoryResponse
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File

class AddStoryViewModel(
    private val storyRepo: StoryRepository,
    private val userRepo: UserRepository
) : ViewModel() {

    private val _addStoryResult = MutableLiveData<AddStoryResponse>()
    val resultAddStory: LiveData<AddStoryResponse> = _addStoryResult

    private val _loadingState = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _loadingState

    private val _errorState = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorState

    fun uploadstory(file: File, description: String) {
        _loadingState.value = true

        viewModelScope.launch {
            val userSession = userRepo.getSession().firstOrNull()
            if (userSession == null || userSession.token.isBlank()) {
                _errorState.value = "User session is invalid or token is missing."
                _loadingState.value = false
                return@launch
            }

            try {
                val uploadResult = storyRepo.uploadStory(description, file)

                uploadResult.fold(
                    onSuccess = { response ->
                        _addStoryResult.value = response
                        Log.d("AddViewModel", "Story uploaded successfully: ${response.message}")
                    },
                    onFailure = { exception ->
                        _errorState.value = "Failed to upload story: ${exception.message}"
                        Log.e("AddViewModel", "Upload error: ${exception.message}")
                    }
                )
            } catch (exception: Exception) {
                _errorState.value = "Unexpected error occurred: ${exception.message}"
                Log.e("AddViewModel", "Exception: ${exception.message}", exception)
            } finally {
                _loadingState.value = false
            }
        }
    }
}
