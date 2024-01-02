package com.example.chat_appication.view.users

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chat_appication.databinding.FragmentUsersBinding
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.PreferenceManager
import com.example.chat_appication.shared.adapter.UserAdapter


class UsersFragment : Fragment() {
    private var _binding: FragmentUsersBinding? = null
    private val viewModel: UsersViewModel by viewModels()
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
        _binding = FragmentUsersBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonAdd.setOnClickListener {
            val action =
                UsersFragmentDirections.actionNavigationUsersToNavigationInvite()
            findNavController().navigate(action)
        }
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
                    val adapter = UserAdapter(
                        requireContext(),
                        it,
                        preferencesManager.getString(Constants.KEY_USER_ID) as String,
                        viewModel.getInvitedUser().toList()
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