package com.example.seatpickerapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.seatpickerapp.R
import com.example.seatpickerapp.databinding.ActivityOrderFoodBinding
import com.example.seatpickerapp.fragments.FoodCategoryFragment
import com.example.seatpickerapp.fragments.FoodItemsFragment
import com.example.seatpickerapp.fragments.FoodRestaurantFragment
import com.example.seatpickerapp.interfaces.Communicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrderFoodActivity : AppCompatActivity(), Communicator {

    private lateinit var binding: ActivityOrderFoodBinding
    private var auth: FirebaseAuth? = null
    val db = FirebaseFirestore.getInstance()

    companion object {
        var currentRestaurant: String = ""
        var currentCategory: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderFoodBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        val foodCategoryFragment = FoodCategoryFragment()
        val foodRestaurantFragment = FoodRestaurantFragment()
        setFragment(foodRestaurantFragment)

        bottomBarRealtimeUpdates()

        binding.cartBottomBar.setOnClickListener {
            startActivity(Intent(this, ViewCartActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun setFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.orderFoodFL, fragment)
            commit()
        }

    override fun passDataCom(id: String, name: String) {
        val transaction = this.supportFragmentManager.beginTransaction()
        val foodCategoryFragment = FoodCategoryFragment()
        val foodItemsFragment = FoodItemsFragment()

        when (id) {
            "restaurant" -> {
                currentRestaurant = name
                //val restaurantBundle = Bundle()
                //restaurantBundle.putString("restaurant", name)
                transaction.replace(R.id.orderFoodFL, foodCategoryFragment)
                //foodCategoryFragment.arguments = restaurantBundle
            }
            "category" -> {
                currentCategory = name
                //val categoryBundle = Bundle()
                //categoryBundle.putString("category", name)
                transaction.replace(R.id.orderFoodFL, foodItemsFragment)
                //foodItemsFragment.arguments = categoryBundle
            }
        }

        transaction.commit()
    }
    
    private fun bottomBarRealtimeUpdates() {
        val userCartRef =
            db.collection("users").document(auth?.uid.toString()).collection("cart")
        userCartRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            querySnapshot?.let {

                if (querySnapshot.size() > 0) {
                    binding.cartBottomBar.visibility = View.VISIBLE
                }
                else {
                    binding.cartBottomBar.visibility = View.INVISIBLE
                }

                var total = 0.0
                var cartItems = 0

                for (document in it) {
                    if (document.get("quantity").toString().toInt() > 1) {
                        total += document["itemPrice"].toString().toDouble() * document.get("quantity").toString().toInt()
                        cartItems += document.get("quantity").toString().toInt()
                    }
                    else {
                        total += document["itemPrice"].toString().toDouble()
                        cartItems += 1
                        Log.d("OrderFoodActivities", document["itemPrice"].toString())
                    }
                }

                val rounded = String.format("%.2f", total)
                binding.bottomBarTotal.text = "€$rounded"
                binding.bottomBarItemsTxtView.text = cartItems.toString()
            }
        }
    }


}