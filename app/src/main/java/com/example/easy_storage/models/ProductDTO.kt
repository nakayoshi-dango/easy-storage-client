package com.example.easy_storage.models

import java.util.Date

data class ProductDTO(
    val id: String,
    val name: String,
    val description: String,
    val uploaderUsername: String,
    val whereToBuy: String,
    val uploadDate: Date,
    val imageURL: String,
)
