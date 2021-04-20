package com.example.seatpickerapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.seatpickerapp.R
import com.example.seatpickerapp.databinding.ActivityTableBookingBinding
import com.example.seatpickerapp.fragments.*
import com.example.seatpickerapp.interfaces.Communicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TableBookingActivity : AppCompatActivity(), Communicator {

    private lateinit var binding: ActivityTableBookingBinding
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore?= null

    companion object {
        var currentTableRestaurant: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTableBookingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val tableRestaurantFragment = TableRestaurantFragment()
        setFragment(tableRestaurantFragment)
    }

    private fun setFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
            replace(R.id.tableBookingFL, fragment)
            commit()
        }

    override fun passDataCom(id: String, name: String) {
        val transaction = this.supportFragmentManager.beginTransaction()
        val flanagansFragment = FlanagansFragment()
        val oakFirePizzaFragment = OakFirePizzaFragment()

        when (name) {
            "flanagans" -> {
                //OrderFoodActivity.currentRestaurant = name
                transaction.replace(R.id.tableBookingFL, flanagansFragment)
            }
            "oakFirePizza" -> {
                //OrderFoodActivity.currentCategory = name
                transaction.replace(R.id.tableBookingFL, oakFirePizzaFragment)
            }
        }

        transaction.commit()
    }

}