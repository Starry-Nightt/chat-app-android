package com.example.chat_appication.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.chat_appication.R
import com.example.chat_appication.databinding.ActivityChatGroupRoomBinding
import com.example.chat_appication.databinding.ActivityChatRoomBinding
import com.example.chat_appication.model.ChatGroupMessage
import com.example.chat_appication.model.ChatMessage
import com.example.chat_appication.model.Group
import com.example.chat_appication.model.User
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.Database
import com.example.chat_appication.shared.PreferenceManager
import com.example.chat_appication.shared.Utils
import com.example.chat_appication.shared.adapter.ChatAdapter
import com.example.chat_appication.shared.adapter.ChatGroupAdapter
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

class ChatGroupRoomActivity : BaseActivity() {
    private lateinit var binding: ActivityChatGroupRoomBinding
    private lateinit var preferencesManager: PreferenceManager
    private lateinit var groupInfo: Group
    private var chatMessages: MutableList<ChatGroupMessage> = mutableListOf()
    private lateinit var chatGroupAdapter: ChatGroupAdapter
    private val userList: MutableList<User> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_group_room)
        preferencesManager = PreferenceManager(applicationContext)
        binding = ActivityChatGroupRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setClickLlistener()
        loadData()
    }

    fun init() {
        Log.d("User", userList.size.toString())
        chatGroupAdapter = ChatGroupAdapter(
            chatMessages,
            userList,
            preferencesManager.getString(Constants.KEY_USER_ID) as String
        )
        binding.groupMessageRecycleView.adapter = chatGroupAdapter
    }

    private fun loadData() {
        groupInfo = intent.getSerializableExtra(Constants.KEY_GROUP_INFO) as Group
        binding.groupnameText.text = groupInfo.name.toString()
        if (groupInfo.groupImage != null) {
            if (groupInfo.groupImage!!.isNotEmpty())
                binding.imageGroup.setImageBitmap(Utils.decodeImage(groupInfo.groupImage!!))
        }
        val memberIds = groupInfo.memberIds


        // CoroutineScope cho vòng đời của activity
        val coroutineScope = CoroutineScope(Dispatchers.Main)

        // List các deferred task, mỗi task tương ứng với một truy vấn
        val deferredList = mutableListOf<Deferred<Unit>>()

        // Thực hiện các truy vấn bất đồng bộ
        memberIds.forEach { userId ->
            val deferred = coroutineScope.async {
                val document = Database.userCollection.document(userId).get().await()
                if (document.exists()) {
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
                    userList.add(userTmp)
                }
            }
            deferredList.add(deferred)
        }
        coroutineScope.launch {
            deferredList.awaitAll()
            init()
            listenMessage()
        }
    }


    private fun listenMessage() {
        Database.chatGroupCollection.whereEqualTo(
            Constants.KEY_CHAT_GROUP_GROUP_RECEIVER_ID,
            groupInfo.id
        ).addSnapshotListener(eventListener)
    }

    private val eventListener = EventListener<QuerySnapshot> { value, error ->
        if (error != null) return@EventListener
        if (value != null) {
            val count = chatMessages.size
            for (documentChange in value.documentChanges) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    val dateTmp =
                        documentChange.document.getDate(Constants.KEY_CHAT_GROUP_TIME_STAMP) as Date
                    val memberIdsField =
                        documentChange.document.get(Constants.KEY_CHAT_GROUP_MEMBER_IDS)
                    if (memberIdsField is List<*>) {
                        val memberIds = memberIdsField.mapNotNull { it as? String }
                        val chatMessage = ChatGroupMessage(
                            senderId = documentChange.document.getString(Constants.KEY_CHAT_GROUP_SENDER_ID)
                                ?: "",
                            memberIds = memberIds,
                            message = documentChange.document.getString(Constants.KEY_CHAT_GROUP_MESSAGE) as String,
                            dateTime = Utils.getReadableDateTime(dateTmp),
                            date = dateTmp,
                            groupId = documentChange.document.getString(Constants.KEY_CHAT_GROUP_GROUP_RECEIVER_ID) as String
                        )
                        chatMessages.add(chatMessage)
                    }

                }
            }
            chatMessages.sortBy { it.date }
            if (count == 0) {
                chatGroupAdapter.notifyDataSetChanged()
            } else {
                chatGroupAdapter.notifyItemRangeInserted(chatMessages.size, chatMessages.size)
                binding.groupMessageRecycleView.smoothScrollToPosition(chatMessages.size - 1)
            }
            binding.groupMessageRecycleView.visibility = View.VISIBLE
        }
        binding.spinner.visibility = View.GONE

    }

    private fun sendMessage() {
        val messagesPayload = mapOf(
            Constants.KEY_CHAT_GROUP_SENDER_ID to preferencesManager.getString(Constants.KEY_USER_ID),
            Constants.KEY_CHAT_GROUP_GROUP_RECEIVER_ID to groupInfo.id,
            Constants.KEY_CHAT_GROUP_MESSAGE to binding.chatInput.text.toString().trim(),
            Constants.KEY_CHAT_GROUP_TIME_STAMP to Date(),
            Constants.KEY_CHAT_GROUP_MEMBER_IDS to groupInfo.memberIds
        )
        Database.chatGroupCollection.add(messagesPayload)
        binding.chatInput.setText("")
    }

    private fun setClickLlistener() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        binding.sendLayout.setOnClickListener {
            sendMessage()
        }
    }
}