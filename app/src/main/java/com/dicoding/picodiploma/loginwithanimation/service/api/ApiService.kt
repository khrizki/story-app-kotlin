package com.dicoding.picodiploma.loginwithanimation.service.api

import com.dicoding.picodiploma.loginwithanimation.service.response.*
import okhttp3.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register (
        @Field("name") userName: String,
        @Field("email") userEmail: String,
        @Field("password") userPassword: String
    ) : Response<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login (
        @Field("email") userEmail: String,
        @Field("password") userPassword: String
    ) : Response<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") authToken: String,
        @Query("page") pageIndex: Int? = null,
        @Query("size") pageSize: Int? = null,
        @Query("location") includeLocation: Int? = null
    ) : Response<StoriesResponse>

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Header("Authorization") authToken: String,
        @Path("id") storyId: String
    ) : Response<DetailStoryResponse>

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Header("Authorization") authToken: String,
        @Part imageFile: MultipartBody.Part,
        @Part("description") storyDescription: RequestBody,
        @Part("lat") latitude: RequestBody? = null,
        @Part("lon") longitude: RequestBody? = null
    ) : Response<AddStoryResponse>
}