package com.example.roam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()

        val signUpButton = findViewById<Button>(R.id.signIn)
        signUpButton.setOnClickListener {
            val nameText = findViewById<EditText>(R.id.name)
            val emailText = findViewById<EditText>(R.id.email)
            val passText = findViewById<EditText>(R.id.password)
            val cpassText = findViewById<EditText>(R.id.confirmPassword)

            val name = nameText.text.toString()
            val email = emailText.text.toString()
            val pass = passText.text.toString()
            val cpass = cpassText.text.toString()

            if(email.isNotEmpty() && name.isNotEmpty() && pass.isNotEmpty() && cpass.isNotEmpty()){
                if(pass == cpass){
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener{
                        if(it.isSuccessful){
                            val user = firebaseAuth.currentUser
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build()

                            user?.updateProfile(profileUpdates)

                            // Navigate to the desired activity (e.g., MainActivity)
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }

        }
    }
}