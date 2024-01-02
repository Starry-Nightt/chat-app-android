package com.example.chat_appication.model


enum class FriendShipStatus {
    NOT_ACCEPTED,
    SENDING,
    ACCEPTED
}
data class FriendShip (val id: String, val senderId: String, val receiverId: String, val status: FriendShipStatus)