package com.example.chat_appication.shared.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chat_appication.R
import com.example.chat_appication.model.Group
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.Database
import com.example.chat_appication.shared.Utils


class GroupAdapter(
    private val context: Context,
    private val groups: List<Group>,
    private val currentUserId: String,
    private val handleLeave: (group: Group, callback: () -> Unit) -> Unit,
    private val onClickItem: ((group: Group) -> Unit)? = null,
): RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {
    class GroupViewHolder(private val view: View) : RecyclerView.ViewHolder(view){
        val groupName: TextView = view.findViewById(R.id.group_name)
        val leaveButton: Button = view.findViewById(R.id.leave_button)
        val container: LinearLayoutCompat = view.findViewById(R.id.group_item_container)
        val groupImage: com.makeramen.roundedimageview.RoundedImageView = view.findViewById(R.id.avatar_group_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupAdapter.GroupViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.group_item, parent, false)
        return GroupAdapter.GroupViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val item = groups[position]
        holder.groupName.text = item.name

        if (item.groupImage != null && item.groupImage.trim().isNotEmpty()){
            holder.groupImage.setImageBitmap(Utils.decodeImage(item.groupImage))
        }

        holder.leaveButton.setOnClickListener {
            handleLeave(item){
                onInvisible(holder)
            }
        }


        holder.itemView.setOnClickListener {
            onClickItem?.let { act -> act(item) }
        }
    }

    override fun getItemCount(): Int {
        return groups.size
    }


    private fun onInvisible(holder: GroupAdapter.GroupViewHolder){
        holder.container.visibility = View.GONE
    }
}