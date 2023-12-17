package com.example.chat_appication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.chat_appication.R
import com.example.chat_appication.databinding.ActivityLoginBinding
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.Database
import com.example.chat_appication.shared.PreferenceManager
import com.example.chat_appication.shared.Utils

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var preferencesManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferencesManager = PreferenceManager(applicationContext)
        binding.toSignupButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.goButton.setOnClickListener {
            signIn()
        }

        val emailField = preferencesManager.getString(Constants.KEY_EMAIL)
        val passwordField = preferencesManager.getString(Constants.KEY_PASSWORD)
        if (emailField != null && passwordField != null) {
            binding.emailEditText.setText(emailField.toString())
            binding.passwordEditText.setText(passwordField.toString())
            signIn()
        }

    }

    private fun signIn() {
        if (!isValidFormDetail()) return;
        loading(true)

        Database.userCollection
            .whereEqualTo(Constants.KEY_EMAIL, binding.emailEditText.text.toString())
            .whereEqualTo(Constants.KEY_PASSWORD, binding.passwordEditText.text.toString()).get()
            .addOnCompleteListener {
                it
                loading(false)
                if (it.isSuccessful && it.result != null) {
                    if (it.result.documents.size != 1) {
                        Utils.showToast(applicationContext, getString(R.string.incorrect_account))
                    } else {
                        val documentSnapshot = it.result.documents[0]
                        preferencesManager.putBoolean(Constants.KEY_IS_SIGN_IN, true)
                        preferencesManager.putString(Constants.KEY_USER_ID, documentSnapshot.id)
                        preferencesManager.putString(
                            Constants.KEY_NAME,
                            documentSnapshot.getString(Constants.KEY_NAME) as String
                        )
                        preferencesManager.putString(
                            Constants.KEY_AVATAR,
                            documentSnapshot.getString(Constants.KEY_AVATAR) as String
                        )
                        preferencesManager.putString(
                            Constants.KEY_EMAIL,
                            documentSnapshot.getString(Constants.KEY_EMAIL) as String
                        )
                        preferencesManager.putString(
                            Constants.KEY_PASSWORD,
                            documentSnapshot.getString(Constants.KEY_PASSWORD) as String
                        )

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }.addOnFailureListener {
                it
                loading(false)
                Utils.showToast(applicationContext, getString(R.string.sign_in_failure))
            }
    }

    private fun isValidFormDetail(): Boolean {
        if (binding.emailEditText.text.toString().trim().isEmpty()) {
            Utils.showToast(applicationContext, getString(R.string.required_email))
            return false;
        } else if (binding.passwordEditText.toString().trim().isEmpty()) {
            Utils.showToast(applicationContext, getString(R.string.required_password))
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailEditText.text).matches()) {
            Utils.showToast(applicationContext, getString(R.string.invalid_email))
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