package com.hilmisatrio.storyku.data.remote.response


import com.google.gson.annotations.SerializedName

data class ResponseDetailStory(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("story")
    val story: DetailStory
)