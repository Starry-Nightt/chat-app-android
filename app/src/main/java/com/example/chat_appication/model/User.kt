package com.example.chat_appication.model

import java.io.Serializable

data class User(
    val id: String,
    val username: String,
    val avatar: String,
    val email: String,
    var token: String
) : Serializable

