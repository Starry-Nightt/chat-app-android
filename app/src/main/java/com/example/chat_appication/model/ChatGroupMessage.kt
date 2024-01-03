package com.example.chat_appication.model

import java.util.Date

data  class ChatGroupMessage (
    val senderId: String,
    val groupId: String,
    val message: String,
    val dateTime: String,
    val date: Date,
    val memberIds: List<String>
)