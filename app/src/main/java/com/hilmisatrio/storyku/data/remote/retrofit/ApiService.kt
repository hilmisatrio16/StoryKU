package com.hilmisatrio.storyku.data.remote.retrofit

import com.hilmisatrio.storyku.data.remote.response.ResponseDetailStory
import com.hilmisatrio.storyku.data.remote.response.ResponseLogin
import com.hilmisatrio.storyku.data.remote.response.ResponseNewStory
import com.hilmisatrio.storyku.data.remote.response.ResponseRegister
import com.hilmisatrio.storyku.data.remote.response.ResponseStories
import com.hilmisatrio.storyku.data.remote.response.Story
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): ResponseRegister

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): ResponseLogin

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ResponseStories

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): ResponseDetailStory

    @GET("stories")
    suspend fun getStoriesFromLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1
    ): ResponseStories

    @Multipart
    @POST("stories")
    suspend fun uploadStoryWithLocation(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody
    ) : ResponseNewStory

    @Multipart
    @POST("stories")
    suspend fun uploadStoryWithoutLocation(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ) : ResponseNewStory


}