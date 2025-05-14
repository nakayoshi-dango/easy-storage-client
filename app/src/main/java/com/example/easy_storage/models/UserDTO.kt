package com.example.easy_storage.models

import java.util.Date

data class UserDTO(
    val id: String,
    val username: String,
    val role: String,
    val creationDate: Date,
    val imageURL: String,
)
