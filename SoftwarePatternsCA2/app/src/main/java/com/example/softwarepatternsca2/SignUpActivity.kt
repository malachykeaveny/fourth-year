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
import com.example.softwarepatternsca2.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    private var mName: EditText? = null
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var mPhoneNo: EditText? = null
    private var mLogin: TextView? = null
    private var mSignUpBtn: Button? = null
    private var userID: String? = null
    private lateinit var binding: ActivitySignUpBinding
    private var db: FirebaseFirestore?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        mName = findViewById(R.id.signup_name)
        mEmail = findViewById(R.id.signup_email)
        mPassword = findViewById(R.id.signup_password)
        mPhoneNo = findViewById(R.id.signup_phone_no)
        mLogin = findViewById(R.id.already_has_acc)
        mSignUpBtn = findViewById(R.id.signup_btn)
        mSignUpBtn!!.setOnClickListener(View.OnClickListener {
            val name = mName!!.getText().toString()
            val phoneNumber = mPhoneNo!!.getText().toString()
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
            auth!!.createUserWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@SignUpActivity, "User has been created", Toast.LENGTH_SHORT)
                            .show()
                        userID = auth!!.currentUser!!.uid
                        val documentReference = db!!.collection("users").document(
                            userID!!
                        )
                        val user: MutableMap<String, Any> = HashMap()
                        user.put("name", name)
                        user.put("phoneNo", phoneNumber)
                        user.put("emailAddress", emailAddress)
                        //user["name"] = name
                        //user["phoneNo"] = phoneNumber
                        //user["emailAddress"] = emailAddress
                        documentReference.set(user).addOnSuccessListener {
                            Log.d("SignUpActivity", "user created for $userID")
                        }.addOnFailureListener { e ->
                            Log.d("SignUpActivity", "Firebase create user error:  $e")
                        }
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                    } else {
                        Toast.makeText(this@SignUpActivity, "Error: " + task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
        })
        if (auth!!.currentUser != null) {
            //auth!!.signOut()
            startActivity(Intent(applicationContext, MainActivity::class.java))
            //startActivity(Intent(applicationContext, Login::class.java))
            finish()
        }
        mLogin!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(applicationContext, LoginActivity::class.java))
        })
    }
}