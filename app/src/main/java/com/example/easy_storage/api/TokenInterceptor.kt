package com.example.easy_storage.api

import com.example.easy_storage.api.auth.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = TokenManager.token
        val originalRequest = chain.request()

        // Solo agrega el header si el token no es nulo y no es una llamada a /auth/**
        return if (token != null && !originalRequest.url.encodedPath.startsWith("/auth")) {
            val newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}
