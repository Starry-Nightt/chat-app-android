package com.example.chat_appication.shared.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_appication.R
import com.example.chat_appication.model.User
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.Database
import java.util.Objects

class ChatUserAdapter(
    private val context: Context,
    private val users: List<User>,
    private val handleDeleteFriend: (user: User, callback: () -> Unit) -> Unit,
    private val onClickItem: ((user: User) -> Unit)? = null,

    ) :
    RecyclerView.Adapter<ChatUserAdapter.ChatUserViewHolder>() {

    class ChatUserViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.username_chat_user_item)
        val statusUser: TextView = view.findViewById(R.id.chat_user_status)
        val avatarUser: com.makeramen.roundedimageview.RoundedImageView =
            view.findViewById(R.id.avatar_chat_user_item)
        val deleteUserButton: ImageButton = view.findViewById(R.id.delete_friend_button)
        val container: LinearLayoutCompat = view.findViewById(R.id.chat_user_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_user_list_item, parent, false)
        return ChatUserViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ChatUserViewHolder, position: Int) {
        val item = users[position]
        val bytes = Base64.decode(item.avatar, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        holder.avatarUser.setImageBitmap(bitmap)
        holder.username.text = item.username
        holder.deleteUserButton.setOnClickListener {
            handleDeleteFriend(item){
                onInvisible(holder)
            }
        }
        holder.itemView.setOnClickListener {
            onClickItem?.let { act -> act(item) }
        }
        var isOnline = false
        Database.userCollection.document(item.id).addSnapshotListener { value, error ->
            if (error != null) return@addSnapshotListener
            if (value != null) {
                if (value.getLong(Constants.KEY_AVAILABILITY) != null) {
                    val available =
                        Objects.requireNonNull(value.getLong(Constants.KEY_AVAILABILITY)) ?: 0
                    isOnline = (available.toInt() == 1)
                }
            }
            if (isOnline) {
                holder.statusUser.text = "Online"
                holder.statusUser.setTextColor(ContextCompat.getColor(context, R.color.success))
            } else {
                holder.statusUser.text = "Offline"
                holder.statusUser.setTextColor(ContextCompat.getColor(context, R.color.dim))
            }
        }
    }

    private fun onInvisible(holder:ChatUserViewHolder){
        holder.container.visibility = View.GONE
    }
}