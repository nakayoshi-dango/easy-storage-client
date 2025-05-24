package com.example.easy_storage.api.collections

import android.util.Log
import com.example.easy_storage.models.CollectionDTO
import com.example.easy_storage.models.ProductCollectionDTO
import com.example.easy_storage.models.UserDTO
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CollectionsRepository(private val api: CollectionsApi) {

    fun getMyCollections(onResult: (List<CollectionDTO>?) -> Unit) {
        api.getMyCollections().enqueue(object : Callback<List<CollectionDTO>> {
            override fun onResponse(
                call: Call<List<CollectionDTO>>,
                response: Response<List<CollectionDTO>>
            ) {
                Log.d("CollectionsRepo", "getMyCollections: ${response}")
                onResult(response.body())
            }

            override fun onFailure(call: Call<List<CollectionDTO>>, t: Throwable) {
                Log.e("CollectionsRepo", "getMyCollections: ${t.message}")
                onResult(null)
            }
        })
    }

    fun getVisibleCollections(onResult: (List<CollectionDTO>?) -> Unit) {
        api.getVisibleCollections().enqueue(object : Callback<List<CollectionDTO>> {
            override fun onResponse(
                call: Call<List<CollectionDTO>>,
                response: Response<List<CollectionDTO>>
            ) {
                Log.d("CollectionsRepo", "getVisibleCollections: ${response}")
                if (!response.isSuccessful) {
                    val empty: List<CollectionDTO> = emptyList()
                    onResult(empty)
                } else {
                    onResult(response.body())
                }
            }

            override fun onFailure(call: Call<List<CollectionDTO>>, t: Throwable) {
                Log.e("CollectionsRepo", "getVisibleCollections: ${t.message}")
                onResult(null)
            }
        })
    }

    fun getProductsInCollection(name: String, onResult: (List<ProductCollectionDTO>?) -> Unit) {
        api.getProductsInCollection(name).enqueue(object : Callback<List<ProductCollectionDTO>> {
            override fun onResponse(
                call: Call<List<ProductCollectionDTO>>,
                response: Response<List<ProductCollectionDTO>>
            ) {
                Log.d("CollectionsRepo", "getProductsInCollection: ${response}")
                if (response.isSuccessful) {
                    if (response.body()
                            ?.equals("No existen productos en esta colección.") == true
                    ) {
                        val empty: List<ProductCollectionDTO> = emptyList()
                        onResult(empty)
                    } else {
                        onResult(response.body())
                    }
                }
            }

            override fun onFailure(call: Call<List<ProductCollectionDTO>>, t: Throwable) {
                Log.e("CollectionsRepo", "getProductsInCollection: ${t.message}")
                onResult(null)
            }
        })
    }

    fun getUsersInCollection(name: String, onResult: (List<UserDTO>?) -> Unit) {
        api.getUsersInCollection(name).enqueue(object : Callback<List<UserDTO>> {
            override fun onResponse(call: Call<List<UserDTO>>, response: Response<List<UserDTO>>) {
                Log.d("CollectionsRepo", "getUsersInCollection: ${response}")
                if (response.isSuccessful) {
                    if (response.body()?.equals("No existen miembros en esta colección.") == true) {
                        val empty: List<UserDTO> = emptyList()
                        onResult(empty)
                    } else {
                        onResult(response.body())
                    }
                }
            }

            override fun onFailure(call: Call<List<UserDTO>>, t: Throwable) {
                Log.e("CollectionsRepo", "getUsersInCollection: ${t.message}")
                onResult(null)
            }
        })
    }

    fun getNonUsersInCollection(collectionName: String, callback: (List<UserDTO>?) -> Unit) {
        val call = api.getNonUsersInCollection(collectionName)
        call.enqueue(object : Callback<List<UserDTO>> {
            override fun onResponse(call: Call<List<UserDTO>>, response: Response<List<UserDTO>>) {
                Log.d("CollectionsRepo", "getNonUsersInCollection: ${response}")
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<UserDTO>>, t: Throwable) {
                Log.e("CollectionsRepo", "getNonUsersInCollection: ${t.message}")
                callback(null)
            }
        })
    }


    fun getCollection(name: String, onResult: (CollectionDTO?) -> Unit) {
        api.getCollection(name).enqueue(object : Callback<CollectionDTO> {
            override fun onResponse(call: Call<CollectionDTO>, response: Response<CollectionDTO>) {
                Log.d("CollectionsRepo", "getCollection: ${response}")
                onResult(response.body())
            }

            override fun onFailure(call: Call<CollectionDTO>, t: Throwable) {
                Log.e("CollectionsRepo", "getCollection: ${t.message}")
                onResult(null)
            }
        })
    }

    fun createCollection(json: JsonObject, onResult: (Boolean) -> Unit) {
        api.createCollection(json).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("CollectionsRepo", "createCollection: ${response}")
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("CollectionsRepo", "createCollection: ${t.message}")
                onResult(false)
            }
        })
    }

    fun updateCollection(json: JsonObject, onResult: (Boolean) -> Unit) {
        api.updateCollection(json).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("CollectionsRepo", "updateCollection: ${response}")
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("CollectionsRepo", "updateCollection: ${t.message}")
                onResult(false)
            }
        })
    }

    fun deleteCollection(name: String, onResult: (Boolean) -> Unit) {
        api.deleteCollection(name).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("CollectionsRepo", "deleteCollection: ${response}")
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("CollectionsRepo", "deleteCollection: ${t.message}")
                onResult(false)
            }
        })
    }

    fun getAllCollections(callback: (List<CollectionDTO>?) -> Unit) {
        api.getAllCollections().enqueue(object : Callback<List<CollectionDTO>> {
            override fun onResponse(
                call: Call<List<CollectionDTO>>,
                response: Response<List<CollectionDTO>>
            ) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    Log.e("CollectionsRepo", "getAllCollections: ${response.code()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<CollectionDTO>>, t: Throwable) {
                Log.e("CollectionsRepo", "getAllCollections: ${t.message}")
                callback(null)
            }
        })
    }

}
