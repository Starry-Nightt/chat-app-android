package com.example.chat_appication.view.chat_group_create

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chat_appication.model.User
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.PreferenceManager
import com.example.chat_appication.shared.Utils
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ChatGroupCreateViewModel(application: Application) : AndroidViewModel(application) {
    private var _users = MutableLiveData<MutableList<User>>()
    val users: LiveData<MutableList<User>> get() = _users
    private val preferenceManager = PreferenceManager(application)
    private val friendIds = mutableSetOf<String>()

    val selectedUserIds: MutableSet<String> = mutableSetOf()
    init {
        refreshData()
    }

    private fun getUsers() {
        Utils.database.collection(Constants.KEY_USERS_COLLECTION).get().addOnCompleteListener {
            it
            val currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)
            if (it.isSuccessful && it.result != null) {
                val usersTmp = mutableListOf<User>()
                for (document in it.result) {
                    if (document.id == currentUserId) continue
                    if (!friendIds.contains(document.id)) continue
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
            }
        }
    }

    fun refreshData(){
        friendIds.clear()
        selectedUserIds.clear()
        viewModelScope.launch {
            async {
                Utils.fetchFriendUser1(preferenceManager.getString(Constants.KEY_USER_ID) as String) {
                    for (document in it) {
                        val friendsId: String = document.get(Constants.KEY_RECEIVER_INVITE_ID) as String
                        friendIds.add(friendsId)
                    }
                }
            }.await()
            async {
                Utils.fetchFriendUser2(preferenceManager.getString(Constants.KEY_USER_ID) as String) {
                    for (document in it) {
                        val friendsId: String = document.get(Constants.KEY_SENDER_INVITE_ID) as String
                        friendIds.add(friendsId)
                    }
                    getUsers()
                }
            }.await()
        }
    }

    fun onCheckUser(user: User, isChecked: Boolean){
        if (isChecked){
            selectedUserIds.add(user.id)
        }
        else {
            selectedUserIds.remove(user.id)
        }
    }
}