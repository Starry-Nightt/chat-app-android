package com.example.chat_appication.model

import java.util.Date

data class ChatMessage(
    val senderId: String,
    val receiverId: String,
    val message: String,
    val dateTime: String,
    val date: Date
)