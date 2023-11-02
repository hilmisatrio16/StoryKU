package com.hilmisatrio.storyku.data.remote.response


import com.google.gson.annotations.SerializedName

data class ResponseLogin(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("loginResult")
    val loginResult: LoginResult,
    @SerializedName("message")
    val message: String
)