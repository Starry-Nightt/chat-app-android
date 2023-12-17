package com.example.chat_appication.view.chat_room

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.chat_appication.R
import com.example.chat_appication.databinding.FragmentChatRoomBinding
import com.example.chat_appication.view.profile.ProfileViewModel

class ChatRoomFragment : Fragment() {
    private var _binding: FragmentChatRoomBinding? = null
    private val viewModel: ChatRoomViewModel by viewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.text.observe(viewLifecycleOwner){
            binding.textTitle.text = it.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}