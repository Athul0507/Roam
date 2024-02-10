package com.example.roam

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 2000 // Splash screen duration in milliseconds (2 seconds)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        // Add a listener to know when the view is about to be drawn
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                rootView.viewTreeObserver.removeOnPreDrawListener(this)

                // After the view is drawn, check isFirstTime and start the next activity
                val isFirstTime = isFirstTime()

                if (isFirstTime) {
                    startActivity(Intent(this@SplashActivity, OnboardingActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                }
                finish() // Close the SplashActivity

                return true
            }
        })
    }

    private fun isFirstTime(): Boolean {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val firstTime = sharedPreferences.getBoolean("firstTime", true)

        if (firstTime) {
            val editor = sharedPreferences.edit()
            editor.putBoolean("firstTime", false)
            editor.apply()
        }

        
        return firstTime
    }
}
