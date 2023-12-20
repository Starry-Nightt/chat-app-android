package com.example.chat_appication.shared.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_appication.R
import com.example.chat_appication.model.User

class InviteAdapter (
    private val users: List<User>,
) :
    RecyclerView.Adapter<InviteAdapter.InviteViewHolder>() {

    class InviteViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.username_invite_item)
        val avatarUser: com.makeramen.roundedimageview.RoundedImageView =
            view.findViewById(R.id.avatar_invite_item)
        val checkButton: ImageButton = view.findViewById(R.id.check_invite)
        val deleteButton: ImageButton = view.findViewById(R.id.delete_invite)

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
    }
}