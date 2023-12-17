package com.example.chat_appication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import com.example.chat_appication.R
import com.example.chat_appication.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animatior)
        val bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animatior)

        binding.logo.startAnimation(topAnim)
        binding.appName.startAnimation(topAnim)
        binding.appSlogan.startAnimation(bottomAnim)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}