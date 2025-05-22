package com.example.easy_storage.api.products

import com.example.easy_storage.models.ProductDTO
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface ProductsApi {

    @GET("/products/getAllProducts")
    fun getAllProducts(): Call<List<ProductDTO>>

    @GET("/products/mine")
    fun getMyProducts(): Call<List<ProductDTO>>

    @GET("/products/mineCount")
    fun getMyProductsCount(): Call<Int>

    @GET("/products/getProduct")
    fun getProduct(@Query("productId") productId: String): Call<ProductDTO>

    @POST("/products/createProduct")
    fun createProduct(@Body json: JsonObject): Call<String>

    @PATCH("/products/updateProduct")
    fun updateProduct(@Body json: JsonObject): Call<String>

    @DELETE("/products/deleteProduct")
    fun deleteProduct(@Query("productId") productId: String): Call<String>

    @PUT("/products/addToCollection")
    fun addToCollection(
        @Query("productId") productId: String,
        @Query("collectionName") collectionName: String,
        @Query("quantity") quantity: Int
    ): Call<String>

    @DELETE("/products/deleteFromCollection")
    fun deleteFromCollection(
        @Query("productId") productId: String,
        @Query("collectionName") collectionName: String
    ): Call<String>
}
