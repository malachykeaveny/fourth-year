package com.example.softwarepatternsca2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.softwarepatternsca2.databinding.ActivityAdminMainBinding
import com.example.softwarepatternsca2.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class AdminMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminMainBinding
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        binding.adminViewItemsBtn.setOnClickListener {
            startActivity(Intent(applicationContext, ViewItemsActivity::class.java))
        }

        binding.checkoutAsCustomerBtn.setOnClickListener {
            startActivity(Intent(applicationContext, CartActivity::class.java))
        }
    }
}