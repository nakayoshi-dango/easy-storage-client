package com.example.easy_storage.api.collections

import com.example.easy_storage.models.CollectionDTO
import com.example.easy_storage.models.ProductCollectionDTO
import com.example.easy_storage.models.UserDTO
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface CollectionsApi {

    @GET("/collections/mine")
    fun getMyCollections(): Call<List<CollectionDTO>>

    @GET("/collections/visible")
    fun getVisibleCollections(): Call<List<CollectionDTO>>

    @GET("/collections/{collectionName}/products")
    fun getProductsInCollection(@Path("collectionName") collectionName: String): Call<List<ProductCollectionDTO>>

    @GET("/collections/{collectionName}/users")
    fun getUsersInCollection(@Path("collectionName") collectionName: String): Call<List<UserDTO>>

    @GET("collections/{collectionName}/nonusers")
    fun getNonUsersInCollection(@Path("collectionName") collectionName: String): Call<List<UserDTO>>

    @GET("/collections/getCollection")
    fun getCollection(@Query("collectionName") collectionName: String): Call<CollectionDTO>

    @POST("/collections/createCollection")
    fun createCollection(@Body json: JsonObject): Call<String>

    @PATCH("/collections/updateCollection")
    fun updateCollection(@Body json: JsonObject): Call<String>

    @DELETE("/collections/deleteCollection")
    fun deleteCollection(@Query("collectionName") collectionName: String): Call<String>
}
