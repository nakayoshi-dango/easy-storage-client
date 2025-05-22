package com.example.easy_storage.api.users

import com.example.easy_storage.models.UserDTO
import retrofit2.Call
import retrofit2.http.*

interface UsersApi {

    @GET("/users/getAllUsers")
    fun getAllUsers(): Call<List<UserDTO>>

    @GET("/users/myInfo")
    fun getMyInfo(): Call<UserDTO>

    @GET("/users/getUser")
    fun getUser(@Query("username") username: String): Call<UserDTO>

    @GET("/users/setPic")
    fun setProfilePicture(@Query("imgURL") imgURL: String): Call<String>

    @PUT("/users/addToCollection")
    fun addToCollection(
        @Query("collectionName") collectionName: String,
        @Query("usernameToAdd") usernameToAdd: String
    ): Call<String>

    @DELETE("/users/deleteFromCollection")
    fun deleteFromCollection(
        @Query("collectionName") collectionName: String,
        @Query("usernameToDelete") usernameToDelete: String
    ): Call<String>
}
