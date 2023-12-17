package com.example.chat_appication.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chat_appication.R
import com.example.chat_appication.databinding.ActivityMainBinding
import com.example.chat_appication.shared.Constants
import com.example.chat_appication.shared.PreferenceManager
import com.example.chat_appication.shared.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import java.util.Objects

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferencesManager = PreferenceManager(applicationContext)
        val navView: BottomNavigationView = binding.navView
        val toolbar: Toolbar = binding.toolBar
        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_chat, R.id.navigation_users, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        getToken()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_logout -> {
            showLogoutDialog()
            true
        }

        else -> {

            super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.logout_title))
            .setMessage(getString(R.string.logout_message))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                signOut()
            }
            .show()
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            updateToken(it)
        }

    }

    private fun updateToken(token: String) {
        val documentReference =
            Utils.database.collection(Constants.KEY_USERS_COLLECTION)
                .document(preferencesManager.getString(Constants.KEY_USER_ID) as String)
        documentReference.update(Constants.KEY_TOKEN, token).addOnFailureListener { e ->
            Utils.showToast(applicationContext, "Unable update token")
        }
    }

    private fun signOut() {
        Utils.showToast(applicationContext, "Signing out...")
        val documentReference =
            Utils.database.collection(Constants.KEY_USERS_COLLECTION)
                .document(preferencesManager.getString(Constants.KEY_USER_ID) as String)
        val updates: Map<String, FieldValue> = mapOf(
            Constants.KEY_TOKEN to FieldValue.delete()
        )
        documentReference.update(updates).addOnSuccessListener {
            preferencesManager.clear()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Utils.showToast(applicationContext, "Unable to sign out!")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }
}