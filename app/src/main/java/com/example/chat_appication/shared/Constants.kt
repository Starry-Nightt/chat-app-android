package com.example.chat_appication.shared

class Constants {
    companion object {
        val KEY_USERS_COLLECTION = "users"
        val KEY_NAME = "username"
        val KEY_EMAIL = "email"
        val KEY_PASSWORD = "password"
        val KEY_PREFERENCE_NAME = "chatAppPreference"
        val KEY_IS_SIGN_IN = "isSignedIn"
        val KEY_USER_ID = "userId"
        val KEY_AVATAR = "avatar"
        val KEY_INVITED_USER_IDS = "invited_user_ids"
        val KEY_TOKEN = "token"
        val KEY_CHAT_USER = "chat_user"
        val KEY_CHAT_COLLECTION = "chat"
        val KEY_SENDER_ID = "senderId"
        val KEY_RECEIVER_ID = "receivedId"
        val KEY_MESSAGE = "message"
        val KEY_TIME_STAMP = "timestamp"
        val KEY_AVAILABILITY = "availability"
        val REMOTE_MSG_AUTHORIZATION = "Authorization"
        val REMOTE_MSG_CONTENT_TYPE = "Content-Type"
        val REMOTE_MSG_DATA = "data"
        val REMOTE_MSG_REGISTRATION_IDS = "registration_ids"
        val KEY_USER = "user"

        val KEY_FRIENDSHIP_COLLECTION = "friendship"
        val KEY_SENDER_INVITE_ID = "senderId"
        val KEY_RECEIVER_INVITE_ID = "receiverId"
        val KEY_FRIENDSHIP_STATUS = "status"

        private var remoteMessageHeader: Map<String, String>? = null

        fun getRemoteMessageHeader(): Map<String, String> {
            return if (remoteMessageHeader == null) {
                remoteMessageHeader = mapOf(
                    REMOTE_MSG_AUTHORIZATION to "key=AAAAs90pO7o:APA91bEt4MD8uObn2R8q39IQ97y5DisPaGHEi7Y7B2zNQgkD_yULzb4h_p74cxS6BJpp3-FTzDbJRscVFhkJVt-yB73ftbBXIOWl94c0SJDg-HCB83H5E-2ggv30-8tYS_65M5bE1Ffj",
                    REMOTE_MSG_CONTENT_TYPE to "application/json"
                )
                remoteMessageHeader as Map<String, String>
            } else {
                remoteMessageHeader as Map<String, String>
            }
        }
    }
}