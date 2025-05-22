package com.example.easy_storage.models

import java.util.Date

data class CollectionDTO(
    val id: Int,
    val name: String,
    val description: String,
    val ownerUsername: String,
    val creationDate: Date,
)