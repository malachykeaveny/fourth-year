package com.example.seatpickerapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.seatpickerapp.databinding.ActivityViewContactsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewContactsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewContactsBinding
    private var auth: FirebaseAuth? = null
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewContactsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()


    }
}