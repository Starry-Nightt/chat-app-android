package com.example.chat_appication.model

import java.io.Serializable

data class Group (val id: String, val name: String, val memberIds: List<String>, val groupImage: String?): Serializable