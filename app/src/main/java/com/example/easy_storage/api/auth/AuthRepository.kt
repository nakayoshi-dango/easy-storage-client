package com.example.easy_storage.api.auth

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(private val authApi: AuthApi) {

    fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
        authApi.login(username, password).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        TokenManager.token = it
                        onResult(true)
                    } ?: onResult(false)
                } else {
                    onResult(false)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.println(Log.WARN,"Error", t.message.toString())
                onResult(false)
            }
        })
    }

    fun register(username: String, password: String, onResult: (Boolean) -> Unit) {
        authApi.register(username, password).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                onResult(false)
            }
        })
    }

    fun validateToken(onResult: (Boolean) -> Unit) {
        val token = TokenManager.token
        if (token == null) {
            onResult(false)
            return
        }

        authApi.validateToken(token).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                onResult(false)
            }
        })
    }
}
