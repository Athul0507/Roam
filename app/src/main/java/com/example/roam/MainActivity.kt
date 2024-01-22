package com.example.roam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Places.initialize(applicationContext, "AIzaSyBglq1rNe7NAt6-Mrv4FxEhO1IaNP8iMrc")
        replaceFrame(Home())
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.action_home -> replaceFrame(Home())
                R.id.action_explore ->replaceFrame(Explore())
                R.id.action_profile -> {
                    if(isUserSignedIn()){
                        replaceFrame(Profile())
                    }
                    else{
                        replaceFrame(Login())
                    }
                }

                else -> {}
            }
            true
        }
    }

    private fun isUserSignedIn(): Boolean{
       val firebaseAuth = FirebaseAuth.getInstance()
        return firebaseAuth.currentUser != null
    }
    private fun replaceFrame(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}