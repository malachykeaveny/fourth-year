package com.example.seatpickerapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class Login : AppCompatActivity() {
    private var mLoginButton: Button?= null
    private var mSignUpButton: TextView? = null
    private var mAdminTxtView: TextView?= null
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        mSignUpButton = findViewById(R.id.signup_txtview)
        mEmail = findViewById(R.id.login_email)
        mPassword = findViewById(R.id.login_password)
        mLoginButton = findViewById(R.id.login_btn)
        mAdminTxtView = findViewById(R.id.admin_textView)

        checkIfLoggedIn()

        mLoginButton!!.setOnClickListener(View.OnClickListener { // validate input
            val emailAddress = mEmail!!.getText().toString().trim { it <= ' ' }
            val password = mPassword!!.getText().toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(emailAddress)) {
                mEmail!!.setError("Email is a required field")
                return@OnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                mPassword!!.setError("Passsword is a required field")
                return@OnClickListener
            }
            if (password.length < 7) {
                mPassword!!.setError("Password must be at least 7 characters")
                return@OnClickListener
            }

            //sign in user
            auth!!.signInWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        checkIfAdmin()
                        //Toast.makeText(this@Login, "User has been logged in", Toast.LENGTH_SHORT).show()
                        //startActivity(Intent(applicationContext, HomePageActivity::class.java))
                    } else {
                        Toast.makeText(
                            this@Login,
                            "Error: " + task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        })
        mSignUpButton!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    SignUpActivity::class.java
                )
            )
        })

        mAdminTxtView!!.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
        }

    }

    private fun checkIfAdmin() {
        lifecycleScope.launch {
            try {
                val userCollectionRef = Firebase.firestore.collection("users")
                val querySnapshot = userCollectionRef.get().await()
                for (document in querySnapshot.documents) {
                    Log.d("adminUid", document.id + " " + auth?.uid.toString())
                    if (document.id == auth?.uid.toString()) {
                        val hasAdminPrivileges = document.get("hasAdminPrivileges")
                        Log.d("hasAdminPriv", hasAdminPrivileges.toString())
                        when (hasAdminPrivileges) {
                            true -> startActivity(Intent(applicationContext, AdminHomeActivity::class.java))
                            false -> startActivity(Intent(applicationContext, HomePageActivity::class.java))
                        }
                    }
                }
            }
            catch (e: Exception) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }
        }
    }

    private fun checkIfLoggedIn() {
        if (auth?.currentUser != null) {
            Log.d("CheckLogin", auth!!.currentUser.toString())
            //startActivity(Intent(applicationContext, HomePageActivity::class.java))
            checkIfAdmin()
        }

    }


}