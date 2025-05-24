package com.example.easy_storage.api.products

import android.util.Log
import com.example.easy_storage.models.ProductDTO
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductsRepository(private val productsApi: ProductsApi) {

    fun getAllProducts(onResult: (List<ProductDTO>?) -> Unit) {
        productsApi.getAllProducts().enqueue(object : Callback<List<ProductDTO>> {
            override fun onResponse(call: Call<List<ProductDTO>>, response: Response<List<ProductDTO>>) {
                Log.e("ProductsRepo","getAllProducts: ${response}" )
                onResult(response.body())
            }

            override fun onFailure(call: Call<List<ProductDTO>>, t: Throwable) {
                Log.e("ProductsRepo", "getAllProducts: ${t.message}")
                onResult(null)
            }
        })
    }

    fun getMyProducts(onResult: (List<ProductDTO>?) -> Unit) {
        productsApi.getMyProducts().enqueue(object : Callback<List<ProductDTO>> {
            override fun onResponse(call: Call<List<ProductDTO>>, response: Response<List<ProductDTO>>) {
                Log.e("ProductsRepo","getMyProducts: ${response}" )
                onResult(response.body())
            }

            override fun onFailure(call: Call<List<ProductDTO>>, t: Throwable) {
                Log.e("ProductsRepo", "getMyProducts: ${t.message}")
                onResult(null)
            }
        })
    }

    fun getMyProductsCount(onResult: (Int?) -> Unit) {
        productsApi.getMyProductsCount().enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                Log.e("ProductsRepo", "getMyProductsCount: ${response}")
                onResult(response.body())
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.e("ProductsRepo", "getMyProductsCount: ${t.message}")
                onResult(null)
            }
        })
    }

    fun getProduct(productId: String, onResult: (ProductDTO?) -> Unit) {
        productsApi.getProduct(productId).enqueue(object : Callback<ProductDTO> {
            override fun onResponse(call: Call<ProductDTO>, response: Response<ProductDTO>) {
                Log.e("ProductsRepo","getProduct: ${response}" )
                onResult(response.body())
            }

            override fun onFailure(call: Call<ProductDTO>, t: Throwable) {
                Log.e("ProductsRepo", "getProduct: ${t.message}")
                onResult(null)
            }
        })
    }

    fun createProduct(json: JsonObject, onResult: (Boolean) -> Unit) {
        productsApi.createProduct(json).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e("ProductsRepo","createProduct: ${response}" )
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("ProductsRepo", "createProduct: ${t.message}")
                onResult(false)
            }
        })
    }

    fun updateProduct(json: JsonObject, onResult: (Boolean) -> Unit) {
        productsApi.updateProduct(json).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e("ProductsRepo","updateProduct: ${response}" )
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("ProductsRepo", "updateProduct: ${t.message}")
                onResult(false)
            }
        })
    }

    fun deleteProduct(productId: String, onResult: (Boolean) -> Unit) {
        productsApi.deleteProduct(productId).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e("ProductsRepo","deleteProduct: ${response}" )
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("ProductsRepo", "deleteProduct: ${t.message}")
                onResult(false)
            }
        })
    }

    fun addToCollection(productId: String, collectionName: String, quantity: Int, onResult: (Boolean) -> Unit) {
        productsApi.addToCollection(productId, collectionName, quantity).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e("ProductsRepo","addToCollection: ${response}" )
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("ProductsRepo", "addToCollection: ${t.message}")
                onResult(false)
            }
        })
    }

    fun deleteFromCollection(productId: String, collectionName: String, onResult: (Boolean) -> Unit) {
        productsApi.deleteFromCollection(productId, collectionName).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e("ProductsRepo","deleteFromCollection: ${response}" )
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("ProductsRepo", "deleteFromCollection: ${t.message}")
                onResult(false)
            }
        })
    }
}
