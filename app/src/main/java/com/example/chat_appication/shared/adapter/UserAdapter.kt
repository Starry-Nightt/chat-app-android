package com.example.chat_appication.shared.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_appication.R
import com.example.chat_appication.model.User

class UserAdapter(
    private val context: Context,
    private val users: List<User>,
    private val onClickItem: ((user: User) -> Unit)? = null
) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val username: TextView = view.findViewById(R.id.username_user_item)
        val statusUser: TextView = view.findViewById(R.id.status)
        val avatarUser: com.makeramen.roundedimageview.RoundedImageView =
            view.findViewById(R.id.avatar_user_item)


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
        if (item.token != null && item.token.isNotEmpty()) {
            holder.statusUser.text = "Online"
            holder.statusUser.setTextColor(ContextCompat.getColor(context, R.color.success))
        } else {
            holder.statusUser.text = "Offline"
            holder.statusUser.setTextColor(ContextCompat.getColor(context, R.color.dim))
        }
        holder.itemView.setOnClickListener {
            onClickItem?.let { act -> act(item) }
        }
    }


    private fun getUserImage(encodeImage: String): Bitmap {
        val bytes = Base64.decode(encodeImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

}