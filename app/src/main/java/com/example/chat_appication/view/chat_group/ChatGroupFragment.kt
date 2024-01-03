package com.example.chat_appication.view.chat_group

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chat_appication.R
import com.example.chat_appication.activities.ChatGroupRoomActivity
import com.example.chat_appication.activities.ChatRoomActivity
import com.example.chat_appication.databinding.FragmentChatGroupBinding
import com.example.chat_appication.model.Group
import com.example.chat_appication.model.User
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.Database
import com.example.chat_appication.shared.PreferenceManager
import com.example.chat_appication.shared.Utils
import com.example.chat_appication.shared.adapter.CheckChatUserAdapter
import com.example.chat_appication.shared.adapter.GroupAdapter
import com.example.chat_appication.view.users.UsersFragmentDirections
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ChatGroupFragment : Fragment() {
    private var _binding: FragmentChatGroupBinding? = null
    private val viewModel: ChatGroupViewModel by viewModels()
    private lateinit var preferencesManager: PreferenceManager
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        preferencesManager = PreferenceManager(requireContext())

        _binding = FragmentChatGroupBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getGroups()
        binding.buttonCreateGroup
            .setOnClickListener {
                val action =
                    ChatGroupFragmentDirections.actionNavigationChatGroupToChatGroupCreateFragment()
                findNavController().navigate(action)
            }
        loading(true)
        viewModel.groups.observe(viewLifecycleOwner) { it ->
            if (it != null) {
                if (it.size > 0) {
                    val adapter = GroupAdapter(requireContext(), it, preferencesManager.getString(Constants.KEY_USER_ID) as String, handleLeave = { group, callback ->
                        confirmLeaveGroup(group, callback)
                    } ){
                        chatToGroup(it)
                    }
                    binding.groupList.adapter = adapter
                    binding.groupList.visibility = View.VISIBLE
                }
            } else {
                binding.groupList.visibility = View.GONE
            }
            loading(false)
        }
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.spinnerGroupList.visibility = View.VISIBLE
        } else {
            binding.spinnerGroupList.visibility = View.INVISIBLE
        }
    }

    private fun confirmLeaveGroup(group: Group, callback: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.logout_title))
            .setMessage(getString(R.string.leave_group_message))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                handleLeaveGroup(group, callback)
            }
            .show()
    }

    private fun handleLeaveGroup(group: Group, callback: () -> Unit){
        val memberIds = group.memberIds.toMutableSet()
        memberIds.remove(preferencesManager.getString(Constants.KEY_USER_ID))
        val updatedData = mapOf(
            Constants.KEY_GROUP_MEMBER_IDS to memberIds.toList()
        )
        if (memberIds.size <= 1){
            Database.groupCollection.document(group.id).delete().addOnSuccessListener {
                Utils.showToast(requireContext(), "Leave group successfully")
                callback()
            }.addOnFailureListener {
                Utils.showToast(requireContext(), "Some errors occurs. Try again!")
            }
        }
        else {
            Database.groupCollection.document(group.id).update(updatedData).addOnSuccessListener {
                Utils.showToast(requireContext(), "Leave group successfully")
                callback()
            }.addOnFailureListener {
                Utils.showToast(requireContext(), "Some errors occurs. Try again!")
            }
        }
    }

    private fun chatToGroup(group: Group) {
        val intent = Intent(requireContext(), ChatGroupRoomActivity::class.java)
        intent.putExtra(Constants.KEY_GROUP_INFO, group)
        startActivity(intent)
    }
}