package com.example.chat_appication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chat_appication.R
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.Database
import com.example.chat_appication.shared.PreferenceManager
import com.google.firebase.firestore.DocumentReference

open class BaseActivity : AppCompatActivity() {
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var documentRef: DocumentReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        preferenceManager = PreferenceManager(applicationContext)
        documentRef = Database.userCollection.document(preferenceManager.getString(Constants.KEY_USER_ID) as String)
    }

    override fun onPause() {
        super.onPause()
        documentRef.update(Constants.KEY_AVAILABILITY, 0)
    }

    override fun onResume() {
        super.onResume()
        documentRef.update(Constants.KEY_AVAILABILITY, 1)
    }
}