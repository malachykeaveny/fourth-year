package com.example.seatpickerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.sign_up_activity.*

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(this);
        //auth = FirebaseAuth.getInstance()

        signup_btn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        login_btn.setOnClickListener {
            doLogin()
        }
    }

    private fun doLogin() {
        if (loginUsername.text.toString().isEmpty()) {
            loginUsername.error = "Enter email"
            loginUsername.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(loginUsername.text.toString()).matches()) {
            loginUsername.error = "Enter a valid email"
            loginUsername.requestFocus()
            return
        }

        if (loginPassword.text.toString().isEmpty()) {
            loginPassword.error = "Enter email"
            loginPassword.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(loginUsername.text.toString(), loginPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                    // ...
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            if(currentUser.isEmailVerified) {
            startActivity(Intent(this,DashboardActivity::class.java))
                finish()
                }
            else {
                Toast.makeText(
                    baseContext, "Please enter a valid email address.",
                    Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Toast.makeText(
                baseContext, "Login failed",
                Toast.LENGTH_SHORT).show()
        }
    }
}