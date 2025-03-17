package com.likhit.userlist.data.remote

import com.likhit.userlist.data.remote.model.UserResponse
import retrofit2.http.GET

interface UserApi {

    @GET("users")
    suspend fun getUsers(): UserResponse

    companion object {
        const val BASE_URL = "https://dummyjson.com/"
    }
}