package com.example.chat_appication.view.users

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chat_appication.model.User
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.PreferenceManager
import com.example.chat_appication.shared.Utils

class UsersViewModel(application: Application) : AndroidViewModel(application) {
    private var _users = MutableLiveData<MutableList<User>>()
    val users: LiveData<MutableList<User>> get() = _users

    private var _message = MutableLiveData<String>("")
    val message: LiveData<String> get() = _message
    private val preferenceManager = PreferenceManager(application)

    init {
        getUsers()
    }

    private fun getUsers() {
        Utils.database.collection(Constants.KEY_USERS_COLLECTION).get().addOnCompleteListener {
            it
            val currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)
            if (it.isSuccessful && it.result != null) {
                val usersTmp = mutableListOf<User>()
                for (document in it.result) {
                    if (document.id == currentUserId) continue
                    val name = document.getString(Constants.KEY_NAME) as String
                    val avatar = document.getString(Constants.KEY_AVATAR) ?: ""
                    val email = document.getString(Constants.KEY_EMAIL) as String
                    val token = document.getString(Constants.KEY_TOKEN) ?: ""
                    val userTmp = User(
                        username = name,
                        avatar = avatar,
                        email = email,
                        token = token,
                        id = document.id
                    )
                    usersTmp.add(userTmp)

                }
                _users.value = usersTmp
                if (usersTmp.size == 0) {
                    _message.value = "No data"
                }
            } else {
                _message.value = "Some error occur. Try again!"
            }
        }
    }
}