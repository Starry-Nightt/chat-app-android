package com.example.chat_appication.view.invite

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chat_appication.model.FriendShipStatus
import com.example.chat_appication.model.User
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.Database
import com.example.chat_appication.shared.PreferenceManager
import com.example.chat_appication.shared.Utils
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FriendInviteViewModel(application: Application) : AndroidViewModel(application) {
    private var _users = MutableLiveData<MutableList<User>>()
    val users: LiveData<MutableList<User>> get() = _users

    private var _message = MutableLiveData<String>("")
    val message: LiveData<String> get() = _message
    private val preferenceManager = PreferenceManager(application)
    private val sendInvitedUserIds = mutableSetOf<String>()

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
                    if (!sendInvitedUserIds.contains(document.id)) continue
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
            } else {
                _message.value = "Some error occur. Try again!"
            }
        }
    }

    fun refreshData(){
        sendInvitedUserIds.clear()
        viewModelScope.launch {
            async {
                Utils.fetchSendInvitedUser(preferenceManager.getString(Constants.KEY_USER_ID) as String) {
                    for (document in it) {
                        val invitedUserId: String =
                            document.get(Constants.KEY_SENDER_INVITE_ID) as String
                        sendInvitedUserIds.add(invitedUserId)
                    }
                    getUsers()

                }
            }.await()
        }

    }

    fun removeInvite(user: User){
        sendInvitedUserIds.remove(user.id)
    }
}