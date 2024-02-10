package com.example.roam
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2


class OnboardingActivity : AppCompatActivity() {

    private val layouts = listOf(
        R.layout.onboarding_page_one,
        R.layout.onboarding_page_two,
        R.layout.onboarding_page_three
    )

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        setOnboardingCompleted()

        viewPager = findViewById(R.id.viewPager)
        adapter = OnboardingAdapter(layouts)
        viewPager.adapter = adapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // Button to go to the next page
        val btnNext = findViewById<Button>(R.id.btnNext)
        btnNext.setOnClickListener {
            if (viewPager.currentItem < adapter.itemCount - 1) {
                viewPager.currentItem += 1
            }
            else{
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }




    }

    private fun setOnboardingCompleted() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Using a key named "onboardingCompleted" to store and retrieve the onboarding status
        val editor = sharedPreferences.edit()
        editor.putBoolean("onboardingCompleted", true)
        editor.apply()
    }
}