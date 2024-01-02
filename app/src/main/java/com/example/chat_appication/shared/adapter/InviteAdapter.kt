package com.example.chat_appication.shared.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_appication.R
import com.example.chat_appication.model.FriendShipStatus
import com.example.chat_appication.model.User
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.Database
import com.example.chat_appication.shared.Utils

class InviteAdapter(
    private val users: List<User>,
    private val currentUserId: String,
) :
    RecyclerView.Adapter<InviteAdapter.InviteViewHolder>() {

    class InviteViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.username_invite_item)
        val avatarUser: com.makeramen.roundedimageview.RoundedImageView =
            view.findViewById(R.id.avatar_invite_item)
        val checkButton: ImageButton = view.findViewById(R.id.check_invite)
        val deleteButton: ImageButton = view.findViewById(R.id.delete_invite)
        val container: LinearLayoutCompat = view.findViewById(R.id.invite_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.invite_list_item, parent, false)
        return InviteViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: InviteViewHolder, position: Int) {
        val item = users[position]
        val bytes = Base64.decode(item.avatar, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        holder.avatarUser.setImageBitmap(bitmap)
        holder.username.text = item.username
        holder.checkButton.setOnClickListener { handleCheck(item, holder) }
        holder.deleteButton.setOnClickListener { handleRemove(item, holder) }
    }

    private fun handleCheck(user: User, holder: InviteViewHolder) {
        Database.friendshipCollection.whereEqualTo(Constants.KEY_SENDER_INVITE_ID, user.id)
            .whereEqualTo(Constants.KEY_RECEIVER_INVITE_ID, currentUserId).get().addOnSuccessListener {
                if (it.size() > 0) {
                    for (document in it) {
                        val docRef = Database.friendshipCollection.document(document.id)
                        val updatedData = mapOf(
                            Constants.KEY_FRIENDSHIP_STATUS to FriendShipStatus.ACCEPTED
                        )
                        docRef.update(updatedData).addOnSuccessListener { onInvisible(holder) }
                    }
                }
            }

        Database.friendshipCollection.whereEqualTo(Constants.KEY_SENDER_INVITE_ID, currentUserId)
            .whereEqualTo(Constants.KEY_RECEIVER_INVITE_ID, user.id).get().addOnSuccessListener {
                if (it.size() > 0) {
                    for (document in it) {
                        val docRef = Database.friendshipCollection.document(document.id)
                        val updatedData = mapOf(
                            Constants.KEY_FRIENDSHIP_STATUS to FriendShipStatus.ACCEPTED
                        )
                        docRef.update(updatedData).addOnSuccessListener { onInvisible(holder) }
                    }
                }
            }

    }

    private fun handleRemove(user: User, holder: InviteViewHolder) {
        Database.friendshipCollection.whereEqualTo(Constants.KEY_SENDER_INVITE_ID, user.id)
            .whereEqualTo(Constants.KEY_RECEIVER_INVITE_ID, currentUserId).get().addOnSuccessListener {
                if (it.size() > 0) {
                    for (document in it) {
                        val docRef = Database.friendshipCollection.document(document.id)
                        docRef.delete().addOnSuccessListener { onInvisible(holder) }
                    }
                }
            }
    }

    private fun onInvisible(holder: InviteViewHolder){
        holder.container.visibility = View.GONE
    }
}