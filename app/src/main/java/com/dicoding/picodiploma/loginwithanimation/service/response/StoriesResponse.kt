package com.dicoding.picodiploma.loginwithanimation.service.response

data class StoriesResponse(
	val error: Boolean,
	val message: String,
	val listStory: List<ListStory>
)

data class ListStory(
	val id: String,
	val name: String,
	val description: String,
	val lat: Double?,
	val lon: Double?,
	val photoUrl: String?,
	val createdAt: String?
)