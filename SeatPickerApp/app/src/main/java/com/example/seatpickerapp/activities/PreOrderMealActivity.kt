package com.example.seatpickerapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.seatpickerapp.R
import com.example.seatpickerapp.databinding.ActivityPreOrderMealBinding
import com.example.seatpickerapp.fragments.FoodCategoryFragment
import com.example.seatpickerapp.fragments.FoodItemsFragment
import com.example.seatpickerapp.fragments.PreOrderCategoryFragment
import com.example.seatpickerapp.fragments.PreOrderItemsFragment
import com.example.seatpickerapp.interfaces.PreOrderCommunicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PreOrderMealActivity : AppCompatActivity(), PreOrderCommunicator {

    private lateinit var binding: ActivityPreOrderMealBinding
    private val db = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth? = null

    companion object {
        var preOrderCategory: String = ""
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreOrderMealBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        val preOrderCategoryFragment = PreOrderCategoryFragment()
        setFragment(preOrderCategoryFragment)


    }

    private fun setFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.preOrderMealFL, fragment)
            commit()
        }

    override fun passDataCommunicator(name: String) {

        if (name != "back") {
            preOrderCategory = name
            val transaction = this.supportFragmentManager.beginTransaction()
            val preOrderItemsFragment = PreOrderItemsFragment()
            transaction.replace(R.id.preOrderMealFL, preOrderItemsFragment)
            transaction.commit()
        }
        else {
            val preOrderCategoryFragment = PreOrderCategoryFragment()
            setFragment(preOrderCategoryFragment)
        }
    }

}