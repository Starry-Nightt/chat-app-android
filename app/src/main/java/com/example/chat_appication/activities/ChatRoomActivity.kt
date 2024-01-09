package com.example.chat_appication.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.chat_appication.R
import com.example.chat_appication.databinding.ActivityChatRoomBinding
import com.example.chat_appication.model.ChatMessage
import com.example.chat_appication.model.User
import com.example.chat_appication.shared.AppClient
import com.example.chat_appication.shared.AppService
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.Database
import com.example.chat_appication.shared.PreferenceManager
import com.example.chat_appication.shared.Utils
import com.example.chat_appication.shared.adapter.ChatAdapter
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import org.checkerframework.checker.nullness.qual.NonNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import java.util.Date
import java.util.Objects

class ChatRoomActivity : BaseActivity() {
    private lateinit var binding: ActivityChatRoomBinding
    private lateinit var chatUser: User
    private lateinit var preferencesManager: PreferenceManager
    private lateinit var chatMessages: MutableList<ChatMessage>
    private lateinit var chatAdapter: ChatAdapter
    private var isChatUserAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        preferencesManager = PreferenceManager(applicationContext)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadUser()
        setClickLlistener()
        init()
        listenMessage()
    }

    private fun init() {
        chatMessages = mutableListOf()
        chatAdapter = ChatAdapter(
            chatMessages,
            Utils.decodeImage(chatUser.avatar),
            preferencesManager.getString(Constants.KEY_USER_ID) as String ?: ""
        )
        binding.recyclerView.adapter = chatAdapter
    }

    private fun listenMessage() {
        Database.chatCollection.whereEqualTo(
            Constants.KEY_SENDER_ID,
            preferencesManager.getString(Constants.KEY_USER_ID)
        )
            .whereEqualTo(Constants.KEY_RECEIVER_ID, chatUser.id)
            .addSnapshotListener(eventListener)

        Database.chatCollection.whereEqualTo(Constants.KEY_SENDER_ID, chatUser.id)
            .whereEqualTo(
                Constants.KEY_RECEIVER_ID,
                preferencesManager.getString(Constants.KEY_USER_ID)
            )
            .addSnapshotListener(eventListener)
    }

    private val eventListener = EventListener<QuerySnapshot> { value, error ->
        if (error != null) return@EventListener
        if (value != null) {
            val count = chatMessages.size
            for (documentChange in value.documentChanges) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    val dateTmp = documentChange.document.getDate(Constants.KEY_TIME_STAMP) as Date
                    val chatMessage = ChatMessage(
                        senderId = documentChange.document.getString(Constants.KEY_SENDER_ID) ?: "",
                        receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                            ?: "",
                        message = documentChange.document.getString(Constants.KEY_MESSAGE) ?: "",
                        dateTime = Utils.getReadableDateTime(dateTmp),
                        date = dateTmp
                    )
                    chatMessages.add(chatMessage)
                }
            }
            chatMessages.sortBy { it.date }
            if (count == 0) {
                chatAdapter.notifyDataSetChanged()
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size, chatMessages.size)
                binding.recyclerView.smoothScrollToPosition(chatMessages.size - 1)
            }
            binding.recyclerView.visibility = View.VISIBLE
        }
        binding.spinner.visibility = View.GONE

    }

    private fun sendMessage() {
        val messagesPayload = mapOf(
            Constants.KEY_SENDER_ID to preferencesManager.getString(Constants.KEY_USER_ID),
            Constants.KEY_RECEIVER_ID to chatUser.id,
            Constants.KEY_MESSAGE to binding.chatInput.text.toString().trim(),
            Constants.KEY_TIME_STAMP to Date(),
        )
        Database.chatCollection.add(messagesPayload)
        if (!isChatUserAvailable) {
            try {
                val tokens = JSONArray()
                tokens.put(chatUser.token)
                Log.d("token", chatUser.token)
                val data = JSONObject()
                data.put(Constants.KEY_USER_ID, preferencesManager.getString(Constants.KEY_USER_ID))
                data.put(Constants.KEY_NAME, preferencesManager.getString(Constants.KEY_NAME))
                data.put(Constants.KEY_TOKEN, preferencesManager.getString(Constants.KEY_TOKEN))
                data.put(Constants.KEY_MESSAGE, binding.chatInput.text.toString())

                val body = JSONObject()
                body.put(Constants.REMOTE_MSG_DATA, data)
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)
                sendNotification(body.toString())
            } catch (e: JSONException) {
                Utils.showToast(applicationContext, "Send notification failed")
            }
        }
        binding.chatInput.setText("")

    }

    private fun loadUser() {
        chatUser = intent.getSerializableExtra(Constants.KEY_CHAT_USER) as User
        binding.usernameText.text = chatUser.username
        binding.imageProfile.setImageBitmap(Utils.decodeImage(chatUser.avatar))

    }

    private fun sendNotification(message: String) {
        AppClient.getClient().create(AppService::class.java).sendMessage(
            Constants.getRemoteMessageHeader(),
            message
        ).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    try {
                        if (response.body() != null) {
                            val responseJson = JSONObject(response.body())
                            val results = responseJson.getJSONArray("results")
                            if (responseJson.getInt("failure") == 1) {
                                val error = results.get(0) as JSONObject
                                Utils.showToast(applicationContext, error.getString("error"))
                                return;
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    Utils.showToast(applicationContext, "Send notification failure")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Utils.showToast(applicationContext, "Some errors occur")

            }
        })
    }

    private fun listenAvailability() {
        Database.userCollection.document(chatUser.id).addSnapshotListener { value, error ->
            if (error != null) return@addSnapshotListener
            if (value != null) {
                if (value.getLong(Constants.KEY_AVAILABILITY) != null) {
                    val available =
                        Objects.requireNonNull(value.getLong(Constants.KEY_AVAILABILITY)) ?: 0
                    isChatUserAvailable = (available.toInt() == 1)
                }
                chatUser.token = value.getString(Constants.KEY_TOKEN) ?: ""
            }
            setStatus()
        }
    }

    private fun setStatus() {
        if (isChatUserAvailable) {
            binding.status.text = "Online"
            binding.status.setTextColor(ContextCompat.getColor(applicationContext, R.color.success))
        } else {
            binding.status.text = "Offline"
            binding.status.setTextColor(ContextCompat.getColor(applicationContext, R.color.light))
        }
    }

    private fun setClickLlistener() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.sendLayout.setOnClickListener {
            sendMessage()
        }
    }

    override fun onResume() {
        super.onResume()
        listenAvailability()
    }
}

