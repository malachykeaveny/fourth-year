package com.example.softwarepatternsca2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.softwarepatternsca2.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        binding.viewItemsBtn.setOnClickListener {
            startActivity(Intent(applicationContext, ViewItemsActivity::class.java))
        }

        binding.logOutBtn.setOnClickListener {
            auth?.signOut()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            Toast.makeText(applicationContext, "User has been logged out", Toast.LENGTH_SHORT).show()
        }
    }
}