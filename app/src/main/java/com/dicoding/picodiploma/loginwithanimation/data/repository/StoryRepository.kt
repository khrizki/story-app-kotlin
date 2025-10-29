package com.dicoding.picodiploma.loginwithanimation.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.dicoding.picodiploma.loginwithanimation.data.user.SessionManager
import com.dicoding.picodiploma.loginwithanimation.data.paging.StoriesPagingSource
import com.dicoding.picodiploma.loginwithanimation.service.response.*
import com.dicoding.picodiploma.loginwithanimation.service.api.ApiService
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository(private val apiService: ApiService, private val userPreference: SessionManager) {
    companion object {
        fun createInstance(api: ApiService, preferences: SessionManager): StoryRepository {
            return StoryRepository(api, preferences)
        }
    }

    suspend fun fetchStories(location: Int? = null,page: Int? = null, size: Int? = null): Result<StoriesResponse> {
        return try {
            val authToken = userPreference.retrieveSession().firstOrNull()?.token
            if (authToken.isNullOrEmpty()) {
                Result.failure<Throwable>(Exception("Authorization token is missing"))
            }

            val result = apiService.getStories("Bearer $authToken", page, size, location)

            if (result.isSuccessful) {
                val body = result.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val error = result.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Error fetching stories: ${result.code()} - $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getPagedStories(): LiveData<PagingData<ListStory>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { StoriesPagingSource(repository = this) }
        ).liveData
    }

    suspend fun fetchStoryDetails(id: String): Result<DetailStoryResponse> {
        return try {
            val authToken = userPreference.retrieveSession().firstOrNull()?.token
            if (authToken.isNullOrEmpty()) {
                Result.failure<Throwable>(Exception("Authorization token is missing"))
            }

            val result = apiService.getDetailStory("Bearer $authToken", id)
            if (result.isSuccessful) {
                val body = result.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("Error fetching story details: ${result.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadStory(
        description: String,
        file: File,
        lat: Double? = null,
        lon: Double? = null
    ): Result<AddStoryResponse> {
        return try {
            val authToken = userPreference.retrieveSession().firstOrNull()?.token
            if (authToken.isNullOrEmpty()) {
                return Result.failure(Exception("Authorization token is missing"))
            }

            val imagePart = file.toMultipartBody()
            val descriptionPart = description.toRequestBody()
            val latPart = lat?.toString()?.toRequestBody()
            val lonPart = lon?.toString()?.toRequestBody()

            val result = apiService.addNewStory("Bearer $authToken", imagePart, descriptionPart, latPart, lonPart)
            if (result.isSuccessful) {
                val body = result.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("Error uploading story: ${result.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun fetchPagedStories(
        location: Int? = null,
        page: Int? = null,
        size: Int? = null
    ): List<ListStory> {
        return try {
            val authToken = userPreference.retrieveSession().firstOrNull()?.token
                ?: throw Exception("Token not found")

            val result = apiService.getStories("Bearer $authToken", page, size, location)

            if (result.isSuccessful) {
                val body = result.body()
                if (body != null) {
                    body.listStory?.filterNotNull() ?: emptyList()
                } else {
                    throw Exception("Empty response body")
                }
            } else {
                val error = result.errorBody()?.string() ?: "Unknown error"
                throw Exception("SError fetching paged stories: ${result.code()} - $error")
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch stories: ${e.message}", e)
        }
    }



    private fun File.toMultipartBody(): MultipartBody.Part {
        val requestBody = this.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("photo", this.name, requestBody)
    }
}