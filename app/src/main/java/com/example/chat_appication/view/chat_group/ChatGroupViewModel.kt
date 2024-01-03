package com.example.chat_appication.view.chat_group

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.chat_appication.model.Group
import com.example.chat_appication.model.User
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.Database
import com.example.chat_appication.shared.PreferenceManager
import com.example.chat_appication.shared.Utils
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ChatGroupViewModel(application:Application) : AndroidViewModel(application) {
    private var _groups = MutableLiveData<MutableList<Group>>()
    val groups: LiveData<MutableList<Group>> get() = _groups
    private val preferenceManager = PreferenceManager(application)

    init {
        getGroups()
    }
    fun getGroups() {
        Database.groupCollection.get().addOnCompleteListener {
            it
            val currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)
            if (it.isSuccessful && it.result != null) {
                val usersTmp = mutableListOf<Group>()
                for (document in it.result) {
                    val name = document.getString(Constants.KEY_GROUP_NAME) as String
                    val memberIdsField = document.get(Constants.KEY_GROUP_MEMBER_IDS)
                    if (memberIdsField is List<*>) {
                        val memberIds = memberIdsField.mapNotNull { it as? String }
                        if (!memberIds.contains(currentUserId)) continue
                        val groupTmp = Group(
                            name = name,
                            memberIds= memberIds.toList(),
                            id = document.id
                        )
                        usersTmp.add(groupTmp)
                    }
                }
                _groups.value = usersTmp
            }
        }
    }

}