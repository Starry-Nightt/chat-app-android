package com.example.chat_appication.view.invite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FriendInviteViewModel: ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is friend invite Fragment"
    }
    val text: LiveData<String> = _text
}