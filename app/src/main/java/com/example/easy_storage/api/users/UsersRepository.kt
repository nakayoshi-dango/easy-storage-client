package com.example.easy_storage.api.users

import android.util.Log
import com.example.easy_storage.models.UserDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsersRepository(private val usersApi: UsersApi) {

    fun getAllUsers(onResult: (List<UserDTO>?) -> Unit) {
        usersApi.getAllUsers().enqueue(object : Callback<List<UserDTO>> {
            override fun onResponse(call: Call<List<UserDTO>>, response: Response<List<UserDTO>>) {
                Log.e("UsersRepo","getAllUsers: $response")
                onResult(response.body())
            }

            override fun onFailure(call: Call<List<UserDTO>>, t: Throwable) {
                Log.e("UsersRepo", "getAllUsers: ${t.message}")
                onResult(null)
            }
        })
    }

    fun getCurrentUser(callback: (UserDTO?) -> Unit) {
        usersApi.getMyInfo().enqueue(object : Callback<UserDTO> {
            override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                if (response.isSuccessful) {
                    Log.e("UsersRepo","getCurrentUser: $response")
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                Log.e("UsersRepo","getCurrentUser: $t")
                callback(null)
            }
        })
    }

    fun getUser(username: String, onResult: (UserDTO?) -> Unit) {
        usersApi.getUser(username).enqueue(object : Callback<UserDTO> {
            override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                Log.e("UsersRepo","getUser: $response")
                onResult(response.body())
            }

            override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                Log.e("UsersRepo", "getUser: ${t.message}")
                onResult(null)
            }
        })
    }

    fun setProfilePicture(imgURL: String, onResult: (Boolean) -> Unit) {
        usersApi.setProfilePicture(imgURL).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e("UsersRepo","setProfilePicture: $response")
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("UsersRepo", "setProfilePicture: ${t.message}")
                onResult(false)
            }
        })
    }

    fun addToCollection(collectionName: String, usernameToAdd: String, onResult: (Boolean) -> Unit) {
        usersApi.addToCollection(collectionName, usernameToAdd).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e("UsersRepo","addToCollection: $response")
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("UsersRepo", "addToCollection: ${t.message}")
                onResult(false)
            }
        })
    }

    fun deleteFromCollection(collectionName: String, usernameToDelete: String, onResult: (Boolean) -> Unit) {
        usersApi.deleteFromCollection(collectionName, usernameToDelete).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e("UsersRepo","deleteFromCollection: $response")
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("UsersRepo", "deleteFromCollection: ${t.message}")
                onResult(false)
            }
        })
    }
}
