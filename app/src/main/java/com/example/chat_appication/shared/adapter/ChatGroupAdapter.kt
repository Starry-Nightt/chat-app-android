package com.example.chat_appication.shared.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_appication.R
import com.example.chat_appication.model.ChatGroupMessage
import com.example.chat_appication.model.User
import com.example.chat_appication.shared.Utils
import com.makeramen.roundedimageview.RoundedImageView

class ChatGroupAdapter(
    private val chatMessages: List<ChatGroupMessage>,
    private val members: List<User>,
    private val senderId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        val VIEW_TYPE_SENT = 1
        val VIEW_TYPE_RECEIVED = 2

    }

    private class SentMessageViewHolder(
        private val view: View,
    ) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.findViewById(R.id.message_sender)
        val messageSentTime: TextView = view.findViewById(R.id.message_sent_time_sender)
    }


    private class ReceivedMessageViewHolder(
        private val view: View,
    ) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.findViewById(R.id.message_receiver)
        val messageSentTime: TextView = view.findViewById(R.id.messageSentTimeReceiver)
        val avatar: RoundedImageView = view.findViewById(R.id.image_profile_receiver)
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT){
            val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_send_message, parent, false)
            SentMessageViewHolder(adapterLayout)
        } else {
            val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_received_message, parent, false)
            ReceivedMessageViewHolder(adapterLayout)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = chatMessages[position]

        if (getItemViewType(position) == ChatAdapter.VIEW_TYPE_SENT){
            (holder as SentMessageViewHolder).message.text =data.message
            holder.messageSentTime.text = data.dateTime
        } else {
            (holder as ReceivedMessageViewHolder).message.text =data.message
            holder.messageSentTime.text = data.dateTime
            val avatar = members.find { user -> user.id == data.senderId }?.avatar
            if (avatar != null){
                holder.avatar.setImageBitmap(Utils.decodeImage(avatar))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position].senderId == senderId) {
            VIEW_TYPE_SENT
        } else
            VIEW_TYPE_RECEIVED
    }
}