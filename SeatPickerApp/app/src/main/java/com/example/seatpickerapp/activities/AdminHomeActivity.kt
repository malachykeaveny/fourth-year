package com.example.seatpickerapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.seatpickerapp.databinding.ActivityAdminHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminHomeBinding
    private var auth: FirebaseAuth? = null

    companion object {
        var adminRestaurantName: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val docRef = db.collection("users").document(auth?.uid.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    adminRestaurantName = document.get("name").toString()
                    Log.d("OrdersFragment", "DocumentSnapshot data: $adminRestaurantName")
                    binding.restaurantNameTxtView.text = adminRestaurantName
                } else {
                    Log.d("OrdersFragment", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("OrdersFragment", "get failed with ", exception)
            }

        binding.changeLayoutCdVw.setOnClickListener {
            startActivity(Intent(applicationContext, ChangeSeatingActivity::class.java))
        }

        binding.assignStaffCdVw.setOnClickListener {
            intent = Intent(this, StaffToTablesActivity::class.java)
            intent.putExtra("restaurant", adminRestaurantName)
            startActivity(intent)
        }

        binding.contTracingCdVw.setOnClickListener {
            //startActivity(Intent(applicationContext, ManageContactTracingActivity::class.java))
            intent = Intent(this, ManageContactTracingActivity::class.java)
            intent.putExtra("restaurant", adminRestaurantName)
            startActivity(intent)
        }

        binding.editMenuCdVw.setOnClickListener {
            intent = Intent(this, EditMenuActivity::class.java)
            intent.putExtra("restaurant", adminRestaurantName)
            startActivity(intent)
        }

        binding.adminLogOut.setOnClickListener {
            logOut()
        }
    }

    private fun logOut() {
        auth?.signOut()
        startActivity(Intent(applicationContext, LoginActivity::class.java))
        Toast.makeText(applicationContext, "Admin has been logged out", Toast.LENGTH_SHORT)
            .show()
    }

}