package com.example.chat_appication.view.chat

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.chat_appication.R
import com.example.chat_appication.activities.ChatRoomActivity
import com.example.chat_appication.databinding.FragmentChatBinding
import com.example.chat_appication.model.User
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.Database
import com.example.chat_appication.shared.PreferenceManager
import com.example.chat_appication.shared.adapter.ChatUserAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var preferencesManager: PreferenceManager

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        preferencesManager = PreferenceManager(requireContext())

        _binding = FragmentChatBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.refreshData()
        loading(true)
        viewModel.users.observe(viewLifecycleOwner) { it ->
            if (it != null) {
                if (it.size > 0) {
                    val adapter =
                        ChatUserAdapter(requireContext(), it, handleDeleteFriend = { user, callback ->
                            confirmDeleteFriend(user, callback)
                        }) {
                            chatToUser(it)
                        }
                    binding.userList.adapter = adapter
                    binding.userList.visibility = View.VISIBLE
                    hideMessage()
                } else {
                    showMessage("No data")
                }
            } else {
                binding.userList.visibility = View.GONE
            }
            viewModel.message.observe(viewLifecycleOwner) {
                showMessage(it.toString())
            }
            loading(false)
        }


    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.spinnerUserList.visibility = View.VISIBLE
        } else {
            binding.spinnerUserList.visibility = View.INVISIBLE
        }
    }

    private fun showMessage(message: String) {
        binding.textMessage.text = message
        binding.textMessage.visibility = View.VISIBLE
    }

    private fun hideMessage() {
        binding.textMessage.text = ""
        binding.textMessage.visibility = View.GONE
    }

    private fun chatToUser(user: User) {
        val intent = Intent(requireContext(), ChatRoomActivity::class.java)
        intent.putExtra(Constants.KEY_CHAT_USER, user)
        startActivity(intent)
    }

    private fun confirmDeleteFriend(user: User, callback: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.logout_title))
            .setMessage(getString(R.string.delete_friend_message))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                handleDeleteFriend(user, callback)
            }
            .show()
    }

    private fun handleDeleteFriend(user: User, callback: () -> Unit) {
        Database.friendshipCollection.whereEqualTo(Constants.KEY_SENDER_INVITE_ID, user.id)
            .whereEqualTo(
                Constants.KEY_RECEIVER_INVITE_ID,
                preferencesManager.getString(Constants.KEY_USER_ID)
            ).get()
            .addOnSuccessListener {
                if (it.size() > 0) {
                    for (document in it) {
                        val docRef = Database.friendshipCollection.document(document.id)
                        docRef.delete()
                    }
                }
            }

        Database.friendshipCollection.whereEqualTo(
            Constants.KEY_SENDER_INVITE_ID,
            preferencesManager.getString(Constants.KEY_USER_ID)
        )
            .whereEqualTo(Constants.KEY_RECEIVER_INVITE_ID, user.id).get()
            .addOnSuccessListener {
                if (it.size() > 0) {
                    for (document in it) {
                        val docRef = Database.friendshipCollection.document(document.id)
                        docRef.delete()
                    }
                }
            }
        callback()

    }
}
