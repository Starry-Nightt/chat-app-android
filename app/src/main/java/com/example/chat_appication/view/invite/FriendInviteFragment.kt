package com.example.chat_appication.view.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.chat_appication.databinding.FragmentFriendInviteBinding
import com.example.chat_appication.view.invite.FriendInviteViewModel

class FriendInviteFragment : Fragment() {
    private var _binding: FragmentFriendInviteBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inviteViewModel =
            ViewModelProvider(this).get(FriendInviteViewModel::class.java)

        _binding = FragmentFriendInviteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textTitle
        inviteViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}