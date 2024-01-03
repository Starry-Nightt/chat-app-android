package com.example.chat_appication.shared.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_appication.R
import com.example.chat_appication.model.User

class CheckChatUserAdapter(
    private val context: Context,
    private val users: List<User>,
    private val onCheckItem: ((user: User, isChecked: Boolean) -> Unit),

): RecyclerView.Adapter<CheckChatUserAdapter.CheckChatUserViewHolder>()  {
    class CheckChatUserViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.username_user_item_check)
        val avatarUser: com.makeramen.roundedimageview.RoundedImageView =
            view.findViewById(R.id.avatar_user_item_check)
        val checkBox: CheckBox = view.findViewById(R.id.checkbox_user)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckChatUserAdapter.CheckChatUserViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.check_user_item, parent, false)
        return CheckChatUserAdapter.CheckChatUserViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: CheckChatUserViewHolder, position: Int) {
        val item = users[position]
        val bytes = Base64.decode(item.avatar, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        holder.avatarUser.setImageBitmap(bitmap)
        holder.username.text = item.username
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            onCheckItem(item, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }
}