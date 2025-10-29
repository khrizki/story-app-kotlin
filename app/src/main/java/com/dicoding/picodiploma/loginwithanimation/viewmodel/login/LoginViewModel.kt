package com.dicoding.picodiploma.loginwithanimation.viewmodel.login

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.data.user.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.repository.UserRepository
import com.dicoding.picodiploma.loginwithanimation.service.response.LoginResponse
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginResult = MutableStateFlow<Result<LoginResponse>?>(null)
    val loginResult: StateFlow<Result<LoginResponse>?> = _loginResult

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

     fun login(email: String, password: String) {
        _loadingState.value = true

        viewModelScope.launch {
            try {
                val response = userRepository.loginUser(email, password)
                _loginResult.value = response
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
                Log.d("AuthenticationViewModel", "Authentication failed: ${e.message}")
            } finally {
                _loadingState.value = false
            }
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            userRepository.saveSession(user)
        }
    }
}
