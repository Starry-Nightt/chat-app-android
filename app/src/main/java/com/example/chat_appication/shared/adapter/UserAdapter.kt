package com.example.chat_appication.shared.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_appication.R
import com.example.chat_appication.model.FriendShipStatus
import com.example.chat_appication.model.User
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.Database
import com.example.chat_appication.shared.Utils
import java.util.Objects

class UserAdapter(
    private val context: Context,
    private val users: List<User>,
    private val currentUserId: String,
    private val invitedUserIds: List<String>,
) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.username_user_item)
        val avatarUser: com.makeramen.roundedimageview.RoundedImageView =
            view.findViewById(R.id.avatar_user_item)
        val addFriendButton: Button = view.findViewById(R.id.add_friend_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false)
        return UserViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = users[position]
        val bytes = Base64.decode(item.avatar, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        holder.avatarUser.setImageBitmap(bitmap)
        holder.username.text = item.username
        holder.addFriendButton.setOnClickListener {
            addFriend(item)
            holder.addFriendButton.isEnabled = false;
            holder.addFriendButton.text = "Sent"
        }
        if (invitedUserIds.contains(item.id)) {
            setButtonStatus(holder, false)
        }
    }

    private fun setButtonStatus(holder: UserViewHolder, active: Boolean) {
        if (active) {
            holder.addFriendButton.isEnabled = true;
            holder.addFriendButton.text = "Add Friend"
        } else {
            holder.addFriendButton.isEnabled = false;
            holder.addFriendButton.text = "Sent"
        }
    }


    private fun addFriend(user: User) {
        Database.friendshipCollection.whereEqualTo(Constants.KEY_SENDER_INVITE_ID, currentUserId)
            .whereEqualTo(Constants.KEY_RECEIVER_INVITE_ID, user.id).get().addOnSuccessListener {
                if (it.isEmpty) {
                    val friendShip = mapOf(
                        Constants.KEY_SENDER_INVITE_ID to currentUserId,
                        Constants.KEY_RECEIVER_INVITE_ID to user.id,
                        Constants.KEY_FRIENDSHIP_STATUS to FriendShipStatus.SENDING
                    )
                    Database.friendshipCollection.add(friendShip).addOnFailureListener {
                        Utils.showToast(context, "Some error occurs, try again!")
                    }
                }
            }
    }
}