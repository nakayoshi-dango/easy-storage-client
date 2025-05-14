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
                Log.e("ProductsRepo","Respuesta: ${response}" )
                onResult(response.body())
            }

            override fun onFailure(call: Call<List<ProductDTO>>, t: Throwable) {
                Log.e("ProductsRepo", "Error al obtener todos los productos: ${t.message}")
                onResult(null)
            }
        })
    }

    fun getMyProducts(onResult: (List<ProductDTO>?) -> Unit) {
        productsApi.getMyProducts().enqueue(object : Callback<List<ProductDTO>> {
            override fun onResponse(call: Call<List<ProductDTO>>, response: Response<List<ProductDTO>>) {
                Log.e("ProductsRepo","Respuesta: ${response}" )
                onResult(response.body())
            }

            override fun onFailure(call: Call<List<ProductDTO>>, t: Throwable) {
                Log.e("ProductsRepo", "Error al obtener tus productos: ${t.message}")
                onResult(null)
            }
        })
    }

    fun getProduct(productId: String, onResult: (ProductDTO?) -> Unit) {
        productsApi.getProduct(productId).enqueue(object : Callback<ProductDTO> {
            override fun onResponse(call: Call<ProductDTO>, response: Response<ProductDTO>) {
                Log.e("ProductsRepo","Respuesta: ${response}" )
                onResult(response.body())
            }

            override fun onFailure(call: Call<ProductDTO>, t: Throwable) {
                Log.e("ProductsRepo", "Error al obtener el producto: ${t.message}")
                onResult(null)
            }
        })
    }

    fun createProduct(json: JsonObject, onResult: (Boolean) -> Unit) {
        productsApi.createProduct(json).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e("ProductsRepo","Respuesta: ${response}" )
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("ProductsRepo", "Error al crear el producto: ${t.message}")
                onResult(false)
            }
        })
    }

    fun updateProduct(json: JsonObject, onResult: (Boolean) -> Unit) {
        productsApi.updateProduct(json).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e("ProductsRepo","Respuesta: ${response}" )
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("ProductsRepo", "Error al editar el producto: ${t.message}")
                onResult(false)
            }
        })
    }

    fun deleteProduct(productId: String, onResult: (Boolean) -> Unit) {
        productsApi.deleteProduct(productId).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e("ProductsRepo","Respuesta: ${response}" )
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("ProductsRepo", "Error al eliminar el producto: ${t.message}")
                onResult(false)
            }
        })
    }

    fun addToCollection(productId: String, collectionName: String, quantity: Int, onResult: (Boolean) -> Unit) {
        productsApi.addToCollection(productId, collectionName, quantity).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e("ProductsRepo","Respuesta: ${response}" )
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("ProductsRepo", "Error al añadir el producto a la colección: ${t.message}")
                onResult(false)
            }
        })
    }

    fun deleteFromCollection(productId: String, collectionName: String, onResult: (Boolean) -> Unit) {
        productsApi.deleteFromCollection(productId, collectionName).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e("ProductsRepo","Respuesta: ${response}" )
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("ProductsRepo", "Error al retirar el producto de la colección: ${t.message}")
                onResult(false)
            }
        })
    }
}
