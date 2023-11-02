package com.hilmisatrio.storyku.data.remote.request

import java.io.File

data class RequestNewStory (
    val imageStoryFile: File,
    val description: String,
    var lat: Float? = null,
    var lon: Float? = null
)