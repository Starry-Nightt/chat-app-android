package com.example.chat_appication.view.chat_group_create

import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chat_appication.R
import com.example.chat_appication.databinding.FragmentChatGroupCreateBinding
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.Database
import com.example.chat_appication.shared.PreferenceManager
import com.example.chat_appication.shared.Utils
import com.example.chat_appication.shared.adapter.ChatUserAdapter
import com.example.chat_appication.shared.adapter.CheckChatUserAdapter
import com.example.chat_appication.view.users.UsersFragmentDirections

class ChatGroupCreateFragment: Fragment()  {
    private var _binding: FragmentChatGroupCreateBinding? = null
    private val viewModel: ChatGroupCreateViewModel by viewModels()
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

        _binding = FragmentChatGroupCreateBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading(true)
        viewModel.users.observe(viewLifecycleOwner) { it ->
            if (it != null) {
                if (it.size > 0) {
                    val adapter = CheckChatUserAdapter(requireContext(), it){ user, isChecked ->
                        viewModel.onCheckUser(user, isChecked)
                    }
                    binding.userListSelect.adapter = adapter
                    binding.userListSelect.visibility = View.VISIBLE
                }
            } else {
                binding.userListSelect.visibility = View.GONE
            }
            loading(false)
        }

        binding.buttonCreateGroup.setOnClickListener {
            handleCreateGroup()
        }

    }

    private fun handleCreateGroup(){
        if (!validateGroup()) return;
        val memberIds = viewModel.selectedUserIds.toMutableList()
        memberIds.add(preferencesManager.getString(Constants.KEY_USER_ID) as String)
        val groupDetail = mapOf(
            Constants.KEY_GROUP_NAME to binding.groupnameEditText.text.toString().trim(),
            Constants.KEY_GROUP_MEMBER_IDS to memberIds
        )
        Database.groupCollection.add(groupDetail).addOnSuccessListener {
            Utils.showToast(requireContext(), getString(R.string.create_group_success))
            findNavController().popBackStack()
        }.addOnFailureListener {
            Utils.showToast(
                requireContext(),
                getString(R.string.create_group_failure)
            )
        }
    }

    private fun validateGroup(): Boolean {
        if (binding.groupnameEditText.text.toString().trim().isEmpty()){
            Utils.showToast(requireContext(), getString(R.string.required_group_name))
            return false;
        }
        if (viewModel.selectedUserIds.size <= 0){
            Utils.showToast(requireContext(), getString(R.string.required_quantity_group_name))
            return false;
        }
        return true;
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.spinnerUserListSelect.visibility = View.VISIBLE
        } else {
            binding.spinnerUserListSelect.visibility = View.INVISIBLE
        }
    }

}