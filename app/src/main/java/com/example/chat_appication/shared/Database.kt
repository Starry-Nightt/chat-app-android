package com.example.chat_appication.shared

import com.google.firebase.firestore.FirebaseFirestore

object Database {
    val instance = FirebaseFirestore.getInstance()

    val userCollection = instance.collection(Constants.KEY_USERS_COLLECTION)

    val chatCollection = instance.collection(Constants.KEY_CHAT_COLLECTION)
}