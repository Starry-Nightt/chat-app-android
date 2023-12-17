package com.example.chat_appication.view.chat_room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ChatRoomViewModel(application: Application) : AndroidViewModel(application) {
    private val _text = MutableLiveData<String>().apply {
        value = "This is chat room Fragment"
    }
    val text: LiveData<String> = _text
}