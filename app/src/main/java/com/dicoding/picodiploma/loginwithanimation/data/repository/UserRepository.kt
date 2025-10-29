package com.dicoding.picodiploma.loginwithanimation.data.repository

import com.dicoding.picodiploma.loginwithanimation.data.user.*
import com.dicoding.picodiploma.loginwithanimation.service.response.*
import com.dicoding.picodiploma.loginwithanimation.service.api.ApiService
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val api: ApiService,
    private val preferences: SessionManager
) {
    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(preferences: SessionManager, api: ApiService): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(api, preferences)
            }.also { instance = it }
    }

    suspend fun createUser(name: String, email: String, password: String): Result<RegisterResponse> {
        return try {
            val result = api.register(name, email, password)
            if (result.isSuccessful) {
                result.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Registration failed with code: ${result.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<LoginResponse> {
        return try {
            val result = api.login(email, password)
            if (result.isSuccessful) {
                result.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Login failed with code: ${result.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveSession(user: UserModel) {
        preferences.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return preferences.retrieveSession()
    }

    suspend fun logout() {
        preferences.clearSession()
    }
}
