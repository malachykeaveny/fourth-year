package com.example.seatpickerapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.seatpickerapp.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        binding.splashImage.animate().translationY(2000F).setDuration(1500).startDelay = 3500
        binding.splashImage2.animate().translationY(2000F).setDuration(1500).startDelay = 3500
        binding.nameImage.animate().translationY(2000F).setDuration(1500).startDelay = 3500
        binding.logoImage.animate().translationY(2200F).setDuration(1500).startDelay = 3500
        binding.backgroundImage.animate().translationY(-2500F).setDuration(1500).startDelay = 3500

        val handler = Handler()
        handler.postDelayed({
            //startActivity(Intent(applicationContext, LoginActivity::class.java))
            checkIfLoggedIn()
        }, 2500)

    }

    private fun checkIfLoggedIn() {
        if (auth?.currentUser != null) {
            Log.d("CheckLogin", auth!!.currentUser.toString())
            //startActivity(Intent(applicationContext, HomePageActivity::class.java))
            checkIfAdmin()
        }
        else {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
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

}