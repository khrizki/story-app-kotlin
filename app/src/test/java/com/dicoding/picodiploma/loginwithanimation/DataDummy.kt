package com.dicoding.picodiploma.loginwithanimation

import com.dicoding.picodiploma.loginwithanimation.service.response.ListStory

object DataDummy {

    fun createSampleStories(): List<ListStory> {
        val storyList: MutableList<ListStory> = mutableListOf()
        for (i in 0 until 100) {
            val story = ListStory(
                photoUrl = "https://sample.com/photo_$i.jpg",
                createdAt = "2024-12-16T10:00:00Z",
                name = "Writer $i",
                description = "This is a sample description for story $i.",
                lon = 100.0 + i,
                id = "story_id_$i",
                lat = -6.0 - i
            )
            storyList.add(story)
        }
        return storyList
    }
}