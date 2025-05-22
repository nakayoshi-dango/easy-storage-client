package com.example.easy_storage.api

import com.example.easy_storage.api.auth.AuthApi
import com.example.easy_storage.api.collections.CollectionsApi
import com.example.easy_storage.api.products.ProductsApi
import com.example.easy_storage.api.users.UsersApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitInstance {

    private var retrofit: Retrofit? = null

    fun init(ip: String, port: String) {
        val baseUrl = "http://$ip:$port/"

        val client = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor())
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun isInit(): Boolean {
        return retrofit != null
    }

    val authApi: AuthApi
        get() = retrofit!!.create(AuthApi::class.java)

    val productsApi: ProductsApi
        get() = retrofit!!.create(ProductsApi::class.java)

    val collectionsApi: CollectionsApi
        get() = retrofit!!.create(CollectionsApi::class.java)

    val usersApi: UsersApi
        get() = retrofit!!.create(UsersApi::class.java)
}

