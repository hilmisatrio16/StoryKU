package com.hilmisatrio.storyku

import com.hilmisatrio.storyku.data.room.Story

object DataDummyStories {
    fun generateDummyStoryResponse(): List<Story> {
        val itemsStory: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                i.toString(),
                "user $i",
                "description story $i",
                1.2 + i,
                5.1 + i,
                "image story $i",
                "11 08 2023 $i"
            )
            itemsStory.add(story)
        }
        return itemsStory
    }
}