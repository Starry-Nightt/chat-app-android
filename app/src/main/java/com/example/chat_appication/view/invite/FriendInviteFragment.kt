package com.example.chat_appication.view.chat

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.chat_appication.activities.ChatRoomActivity
import com.example.chat_appication.databinding.FragmentChatBinding
import com.example.chat_appication.databinding.FragmentFriendInviteBinding
import com.example.chat_appication.model.User
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.PreferenceManager
import com.example.chat_appication.shared.Utils
import com.example.chat_appication.shared.adapter.ChatUserAdapter
import com.example.chat_appication.shared.adapter.InviteAdapter
import com.example.chat_appication.view.invite.FriendInviteViewModel
import com.example.chat_appication.view.users.UsersViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FriendInviteFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val viewModel: FriendInviteViewModel by viewModels()
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
        observeViewModel()

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

    private fun observeViewModel() {
        viewModel.users.observe(viewLifecycleOwner) { it ->
            if (it != null) {
                if (it.size > 0) {
                    val adapter = InviteAdapter(
                        it,
                        preferencesManager.getString(Constants.KEY_USER_ID) as String
                    )
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


}