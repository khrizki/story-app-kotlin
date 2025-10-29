package com.dicoding.picodiploma.loginwithanimation.viewmodel.signup

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.data.repository.UserRepository
import com.dicoding.picodiploma.loginwithanimation.service.response.RegisterResponse
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _signupResult = MutableStateFlow<Result<RegisterResponse>?>(null)
    val signupResult: StateFlow<Result<RegisterResponse>?> = _signupResult

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    fun registerUser(userName: String, userEmail: String, userPassword: String) {
        _loadingState.value = true

        viewModelScope.launch {
            try {
                _signupResult.value = userRepository.createUser(userName, userEmail, userPassword)
            } catch (exception: Exception) {
                Log.d("SignupViewModel", "Registration failed: ${exception.message}")
            } finally {
                _loadingState.value = false
            }
        }
    }
}
