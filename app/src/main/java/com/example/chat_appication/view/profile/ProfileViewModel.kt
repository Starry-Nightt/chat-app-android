package com.example.chat_appication.view.profile

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.PreferenceManager
import com.example.chat_appication.shared.Utils

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private var _avatar = MutableLiveData<Bitmap>()
    val avatar: LiveData<Bitmap> get() = _avatar

    private var _username = MutableLiveData<String>("")
    val username: LiveData<String> get() = _username

    private var _email = MutableLiveData<String>("")
    val email: LiveData<String> get() = _email

    private val preferenceManager = PreferenceManager(application)

    init {
        loadUserData()
    }

    private fun loadUserData() {
        _username.value = preferenceManager.getString(Constants.KEY_NAME)
        val bytes = Base64.decode(preferenceManager.getString(Constants.KEY_AVATAR), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        _avatar.value = bitmap
        _email.value = preferenceManager.getString(Constants.KEY_EMAIL)
    }

    fun updateName(name: String) {
        _username.value = name
    }

    fun updateAvatar(bitmap: Bitmap) {
        _avatar.value = bitmap
    }

    fun updateProfileToDB(callbackSuccess: () -> Unit, callbackFailure: () -> Unit) {
        val avatarSrc = Utils.encodeImage(avatar.value as Bitmap)
        val documentReference = Utils.database.collection(Constants.KEY_USERS_COLLECTION)
            .document(preferenceManager.getString(Constants.KEY_USER_ID) as String)
        val updateData = mapOf(
            Constants.KEY_AVATAR to avatarSrc,
            Constants.KEY_NAME to username.value
        )

        documentReference.update(
            updateData
        ).addOnSuccessListener {
            callbackSuccess()
            preferenceManager.putString(Constants.KEY_NAME, username.value as String)
            preferenceManager.putString(Constants.KEY_AVATAR, avatarSrc)
        }
            .addOnFailureListener {
                callbackFailure
            }

    }
}