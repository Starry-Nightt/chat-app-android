package com.example.chat_appication.view.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.chat_appication.R
import com.example.chat_appication.databinding.FragmentProfileBinding
import com.example.chat_appication.shared.Utils
import java.io.FileNotFoundException

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val viewModel: ProfileViewModel by viewModels()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var encodedImageBitmap: Bitmap
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.usernameEditText.setText(viewModel.username.value)
        binding.emailEditText.setText(viewModel.email.value)
        binding.imageProfile.setImageBitmap(viewModel.avatar.value)
        encodedImageBitmap = viewModel.avatar.value as Bitmap
        binding.cancelButton.setOnClickListener {
            onCancel()
        }
        binding.updateButton.setOnClickListener {
            onUpdate()
        }
        binding.saveButton.setOnClickListener {
            onSave()
        }
        binding.uploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
        viewModel.username.observe(viewLifecycleOwner) { name ->
            binding.usernameText.text = name
        }
        viewModel.email.observe(viewLifecycleOwner) { email ->
            binding.emailText.text = email
        }
        viewModel.avatar.observe(viewLifecycleOwner) { bitmap ->
            binding.avatar.setImageBitmap(bitmap)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onSave() {
        toggleDisableForm(true)
        viewModel.updateAvatar(encodedImageBitmap)
        viewModel.updateName(binding.usernameEditText.text.toString())
        viewModel.updateProfileToDB(callbackSuccess = {
            Utils.showToast(requireContext(), getString(R.string.successful_update))
            toggleDisableForm(false)
            onCancel()
        }, callbackFailure = {
            toggleDisableForm(false)
            Utils.showToast(requireContext(), getString(R.string.successful_failure))
        })

    }



    private fun onCancel() {
        binding.profileDetail.visibility = View.VISIBLE
        binding.profileForm.visibility = View.GONE
    }

    private fun onUpdate() {
        binding.profileForm.visibility = View.VISIBLE
        binding.profileDetail.visibility = View.GONE
    }

    private fun toggleDisableForm(value: Boolean){
        binding.uploadButton.isEnabled = !value;
        binding.saveButton.isEnabled = !value
        binding.cancelButton.isEnabled = !value
        binding.usernameEditText.isEnabled = !value
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data?.data != null) {
                val selectedImageUri = data.data
                try {
                    val inputStream =
                        requireContext().contentResolver.openInputStream(selectedImageUri as Uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.imageProfile.setImageBitmap(bitmap)
                    encodedImageBitmap = bitmap
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }
}