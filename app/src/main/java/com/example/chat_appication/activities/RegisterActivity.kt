package com.example.chat_appication.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.example.chat_appication.databinding.ActivityRegisterBinding
import com.example.chat_appication.shared.Utils
import android.util.Patterns
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import com.example.chat_appication.R
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.PreferenceManager
import java.io.FileNotFoundException
import java.io.InputStream

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var preferencesManager: PreferenceManager
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var encodedImage: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferencesManager = PreferenceManager(applicationContext)
        binding.toSigninButton.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.goButton.setOnClickListener {
            signUp()
        }

        binding.imageProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
    }

    private fun signUp() {
        if (!isValidFormDetail()) return;
        loading(true)
        val email = binding.emailEditText.text.toString()

        Utils.database.collection(Constants.KEY_USERS_COLLECTION)
            .whereEqualTo(Constants.KEY_EMAIL, email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    loading(false)
                    Utils.showToast(
                        applicationContext,
                        getString(R.string.existed_email)
                    )
                } else {
                    val detail = mapOf(
                        Constants.KEY_NAME to binding.usernameEditText.text.toString(),
                        Constants.KEY_EMAIL to email,
                        Constants.KEY_PASSWORD to binding.passwordEditText.text.toString(),
                        Constants.KEY_AVATAR to encodedImage
                    )

                    Utils.database.collection(Constants.KEY_USERS_COLLECTION).add(detail)
                        .addOnSuccessListener { documentReference ->
                            loading(false)
                            preferencesManager.putBoolean(Constants.KEY_IS_SIGN_IN, true)
                            preferencesManager.putString(
                                Constants.KEY_USER_ID,
                                documentReference.id
                            )
                            preferencesManager.putString(
                                Constants.KEY_NAME,
                                binding.usernameEditText.text.toString()
                            )
                            preferencesManager.putString(Constants.KEY_AVATAR, encodedImage)
                            preferencesManager.putString(Constants.KEY_EMAIL, binding.emailEditText.text.toString())

                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                            Utils.showToast(
                                applicationContext,
                                getString(R.string.sign_in_successful)
                            )
                        }
                        .addOnFailureListener { e ->
                            loading(false)
                            Utils.showToast(
                                applicationContext,
                                getString(R.string.sign_up_failure)
                            )
                        }
                }
            }
            .addOnFailureListener { e ->
                loading(false)
                Utils.showToast(applicationContext, "Error checking email existence.")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // Xử lý hình ảnh đã chọn
            if (data?.data != null) {
                val selectedImageUri = data.data
                try {
                    val inputStream = contentResolver.openInputStream(selectedImageUri as Uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.imageProfile.setImageBitmap(bitmap)
                    binding.uploadText.visibility = View.INVISIBLE
                    encodedImage = Utils.encodeImage(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun isValidFormDetail(): Boolean {
        if (encodedImage == null) {
            Utils.showToast(applicationContext, getString(R.string.required_image))
            return false
        } else if (binding.usernameEditText.text.toString().trim().isEmpty()) {
            Utils.showToast(applicationContext, getString(R.string.required_username))
            return false
        } else if (binding.emailEditText.text.toString().trim().isEmpty()) {
            Utils.showToast(applicationContext, getString(R.string.required_email))
            return false;
        } else if (binding.passwordEditText.toString().trim().isEmpty()) {
            Utils.showToast(applicationContext, getString(R.string.required_password))
            return false;
        } else if (binding.confirmPasswordEditText.toString().trim().isEmpty()) {
            Utils.showToast(applicationContext, getString(R.string.required_email))
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailEditText.text).matches()) {
            Utils.showToast(applicationContext, getString(R.string.invalid_email))
            return false;
        } else if (binding.confirmPasswordEditText.text.toString() != binding.passwordEditText.text.toString()
        ) {
            Utils.showToast(applicationContext, getString(R.string.confirm_password_not_match))
            return false;
        }
        return true;
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.spinner.visibility = View.VISIBLE
            binding.goButton.visibility = View.INVISIBLE
        } else {
            binding.spinner.visibility = View.INVISIBLE
            binding.goButton.visibility = View.VISIBLE
        }
    }
}