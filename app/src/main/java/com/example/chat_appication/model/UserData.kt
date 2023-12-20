package com.example.chat_appication.model

import java.io.Serializable

data class UserData(
    val id: String,
    val username: String,
    var token: String
): Serializable