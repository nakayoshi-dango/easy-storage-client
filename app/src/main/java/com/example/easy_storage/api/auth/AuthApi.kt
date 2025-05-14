package com.example.easy_storage.api.auth

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    @FormUrlEncoded
    @POST("/auth/register")
    fun register(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/auth/login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<String>

    @GET("/auth/token")
    fun validateToken(
        @Query("token") token: String
    ): Call<String>
}
