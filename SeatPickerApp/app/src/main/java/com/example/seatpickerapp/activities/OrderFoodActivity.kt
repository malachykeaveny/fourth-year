package com.example.seatpickerapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.seatpickerapp.R
import com.example.seatpickerapp.databinding.ActivityOrderFoodBinding
import com.example.seatpickerapp.fragments.FoodCategoryFragment
import com.example.seatpickerapp.fragments.FoodItemsFragment
import com.example.seatpickerapp.fragments.FoodRestaurantFragment
import com.example.seatpickerapp.interfaces.Communicator
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class OrderFoodActivity : AppCompatActivity(), Communicator {

    private lateinit var binding: ActivityOrderFoodBinding
    private var auth: FirebaseAuth? = null
    val db = FirebaseFirestore.getInstance()

    companion object {
        var currentRestaurant: String = ""
        var currentCategory: String = ""
        val REQUEST_PERMISSION_REQUEST_CODE = 2020
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

        /*binding.buttonGetLocation.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@OrderFoodActivity,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    , REQUEST_PERMISSION_REQUEST_CODE)
            } else {
                getCurrentLocation()
            }
        }

         */

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_REQUEST_CODE && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
            else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {

        var geoCoder = Geocoder(this@OrderFoodActivity, Locale.getDefault())
        var addressList: List<Address>
        var locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority - LocationRequest.PRIORITY_HIGH_ACCURACY


        LocationServices.getFusedLocationProviderClient(this@OrderFoodActivity)
            .requestLocationUpdates(locationRequest,object : LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(this@OrderFoodActivity)
                        .removeLocationUpdates(this)
                    if (locationResult != null && locationResult.locations.size > 0) {
                        var locIndex = locationResult.locations.size - 1
                        var latitude = locationResult.locations.get(locIndex).latitude
                        var longitude = locationResult.locations.get(locIndex).longitude

                        addressList = geoCoder.getFromLocation(latitude,longitude, 1)

                        if (addressList.isNotEmpty()) {
                            var userAddress: String = addressList[0].getAddressLine(0)
                            Log.d("OrderFoodLocation", "Latitude: ${locationResult.locations.get(locIndex).latitude}, Longitude: ${locationResult.locations.get(locIndex).longitude}, address: $userAddress")
                        }
                    }
                }
            }, Looper.getMainLooper())
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