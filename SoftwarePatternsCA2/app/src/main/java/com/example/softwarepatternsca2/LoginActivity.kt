package com.example.softwarepatternsca2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.softwarepatternsca2.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var mLoginButton: Button?= null
    private var mSignUpButton: TextView? = null
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var auth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()
        mSignUpButton = findViewById(R.id.signup_txtview)
        mEmail = findViewById(R.id.login_email)
        mPassword = findViewById(R.id.login_password)
        mLoginButton = findViewById(R.id.login_btn)

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
                        //Toast.makeText(this@LoginActivity, "User has been logged in", Toast.LENGTH_SHORT).show()
                            checkIfAdmin()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
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


    }

    private fun checkIfAdmin() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userCollectionRef = db.collection("users")
                val querySnapshot = userCollectionRef.get().await()
                for (document in querySnapshot.documents) {
                    Log.d("adminUid", document.id + " " + auth?.uid.toString())
                    if (document.id == auth?.uid.toString()) {
                        val hasAdminPrivileges = document.get("hasAdminPrivileges")
                        Log.d("hasAdminPriv", hasAdminPrivileges.toString())
                        when (hasAdminPrivileges) {
                            true -> startActivity(Intent(applicationContext, AdminMainActivity::class.java))
                            false -> startActivity(Intent(applicationContext, MainActivity::class.java))
                        }
                    }
                }
            }
            catch (e: Exception) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}