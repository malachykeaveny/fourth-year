package com.example.seatpickerapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.sign_up_activity.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_activity)
        auth = FirebaseAuth.getInstance()

        sign_up_btn.setOnClickListener {
            signUp()
        }
    }

    fun signUp() {
        if (signUpUsername.text.toString().isEmpty()) {
            signUpUsername.error = "Enter email"
            signUpUsername.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(signUpUsername.text.toString()).matches()) {
            signUpUsername.error = "Enter a valid email"
            signUpUsername.requestFocus()
            return
        }

        if (signUpPassword.text.toString().isEmpty()) {
            signUpPassword.error = "Enter password"
            signUpPassword.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(signUpUsername.text.toString(), signUpPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user!!.sendEmailVerification()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(this,Login::class.java))
                                finish()
                            }
                        }
                    startActivity(Intent(this,Login::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Sign up failed.",
                        Toast.LENGTH_SHORT).show()
                }

                // ...
            }
    }
}