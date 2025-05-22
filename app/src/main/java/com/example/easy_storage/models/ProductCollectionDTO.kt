package com.example.easy_storage.models

import java.util.Date


data class ProductCollectionDTO(
    val productId: String,
    val name: String,
    val description: String,
    val uploaderUsername: String,
    val whereToBuy: String,
    val creationDate: Date,
    val uploadDate: Date,
    val imageURL: String,
    val quantity: Int
)