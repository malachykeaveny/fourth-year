package com.example.seatpickerapp.fragments

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seatpickerapp.R
import com.example.seatpickerapp.activities.OrderFoodActivity
import com.example.seatpickerapp.dataClasses.FoodRestaurant
import com.example.seatpickerapp.dataClasses.User
import com.example.seatpickerapp.databinding.FragmentFoodRestaurantBinding
import com.example.seatpickerapp.interfaces.Communicator
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.w3c.dom.Text
import java.util.*

class FoodRestaurantFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    private var adapter: FoodRestaurantFragment.ProductFirestoreRecyclerAdapter? = null
    private var _binding: FragmentFoodRestaurantBinding? = null
    private val binding get() = _binding!!
    val db = FirebaseFirestore.getInstance()
    private lateinit var communicator: Communicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodRestaurantBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val restaurantRef = db.collection("restaurants")
        binding.restaurantRV.layoutManager = LinearLayoutManager(context)
        val options = FirestoreRecyclerOptions.Builder<FoodRestaurant>().setQuery(
            restaurantRef,
            FoodRestaurant::class.java
        ).build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.restaurantRV.adapter = adapter
        communicator = activity as Communicator

        getAddress()

        //binding.setUserAddressButton.setOnClickListener {
        //addAddress()
        //}

        binding.getLocationButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    OrderFoodActivity.REQUEST_PERMISSION_REQUEST_CODE
                )
            } else {
                getCurrentLocation()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == OrderFoodActivity.REQUEST_PERMISSION_REQUEST_CODE && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {

        var geoCoder = Geocoder(context, Locale.getDefault())
        var addressList: List<Address>
        var locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority - LocationRequest.PRIORITY_HIGH_ACCURACY


        LocationServices.getFusedLocationProviderClient(requireActivity())
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(requireActivity())
                        .removeLocationUpdates(this)
                    if (locationResult != null && locationResult.locations.size > 0) {
                        var locIndex = locationResult.locations.size - 1
                        var latitude = locationResult.locations.get(locIndex).latitude
                        var longitude = locationResult.locations.get(locIndex).longitude

                        addressList = geoCoder.getFromLocation(latitude, longitude, 1)

                        if (addressList.isNotEmpty()) {
                            var userAddress: String = addressList[0].getAddressLine(0)
                            binding.userAddressTxtView.text = userAddress
                            Log.d("FoodRestaurantLocation", "Latitude: ${locationResult.locations.get(locIndex).latitude}, Longitude: ${locationResult.locations.get(locIndex).longitude}, address: $userAddress")

                            val userDocRef = db.collection("users").document(auth?.uid.toString())
                            val user = User()
                            user.latitude = latitude
                            user.longitude = longitude
                            user.userAddress = userAddress

                            userDocRef
                                .update("latitude", latitude, "longitude", longitude, "userAddress", userAddress)
                                .addOnSuccessListener {
                                    Log.d("FoodRestaurantLocation", "DocumentSnapshot successfully updated!")
                                    adapter!!.stopListening()
                                    adapter!!.startListening()
                                }
                                .addOnFailureListener { e -> Log.w("FoodRestaurantLocation", "Error updating document", e) }
                        } else {
                            getCurrentLocation()
                        }
                    }
                }
            }, Looper.getMainLooper())
    }

    private fun getAddress() {
        lifecycleScope.launch {
            try {
                val userCollectionRef = Firebase.firestore.collection("users")
                val querySnapshot = userCollectionRef.get().await()
                for (document in querySnapshot.documents) {
                    //Log.d("FoodRestaurantFragment", document.id + " " + auth?.uid.toString())
                    if (document.id == auth?.uid.toString()) {
                        if (document.get("userAddress").toString().isNotEmpty()) {
                            val userAddress = document.get("userAddress").toString()
                            Log.d("FoodRestaurantFragment", "$userAddress")
                            var addressLine1 = userAddress.split(",")[0]
                            var addressLine2 = userAddress.split(",")[1]
                            var addressLine3 = userAddress.split(",")[2]
                            binding.userAddressTxtView.text = "Delivering to: $addressLine1,$addressLine2,$addressLine3"
                            var userLatitude = document.get("latitude").toString().toDouble()
                            var userLongitude = document.get("longitude").toString().toDouble()
                            //calculateDistance(document.get("latitude").toString().toDouble(), document.get("longitude").toString().toDouble())
                        } else {
                            binding.userAddressTxtView.text = "Please set your location"
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateDistance(latitude: Double, longitude: Double) {
        Log.d("RestCheckLatLong", "$latitude $longitude")


        val startPoint = Location("locationA")
        startPoint.latitude = latitude
        startPoint.longitude = longitude

        val endPoint = Location("locationA")
        endPoint.latitude = 53.35023178444848
        endPoint.longitude = -6.259997307935073

        val distance = startPoint.distanceTo(endPoint).toDouble()
        Log.d("RestCheckLatLong", "$distance")
    }

    private fun addAddress() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add address")
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_address, null)
        val addressLine1 = view.findViewById<EditText>(R.id.address1)
        val addressLine2 = view.findViewById<EditText>(R.id.address2)
        val addressLine3 = view.findViewById<EditText>(R.id.address3)
        val addressCounty = view.findViewById<EditText>(R.id.addressCounty)
        val eirCode = view.findViewById<EditText>(R.id.eirCodeEt)

        builder.setView(view)
        builder.setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton("Done", null)

        val dialog = builder.create()
        dialog.show()

        //Log.d("FoodItemsFragment", "$name ${price.toString()}")
        val confirmButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        confirmButton.setOnClickListener {
            when {
                addressLine1.text.isEmpty() -> addressLine1.setError("Cannot be empty")
                addressCounty.text.isEmpty() -> addressCounty.setError("Cannot be empty")
                eirCode.text.isEmpty() -> eirCode.setError("Cannot be empty")
                else -> {
                    val addressData = hashMapOf(
                        "addressLine1" to addressLine1.text.toString(),
                        "addressLine2" to addressLine2.text.toString(),
                        "addressLine3" to addressLine3.text.toString(),
                        "addressCounty" to addressCounty.text.toString(),
                        "eirCode" to eirCode.text.toString()
                    )

                    Log.d("addAddressCheck", addressData.toString())
                    val addressDocRef = db.collection("users").document(auth?.uid.toString())

                    addressDocRef.update(
                        "addressLine1", addressLine1.text.toString(),
                        "addressLine2", addressLine2.text.toString(),
                        "addressLine3", addressLine3.text.toString(),
                        "addressCounty", addressCounty.text.toString(),
                        "addressEircode", eirCode.text.toString()
                    )
                        .addOnSuccessListener {
                            Log.d(
                                "FoodRestaurantFragment",
                                "DocumentSnapshot successfully updated!"
                            )
                            Toast.makeText(
                                requireContext(),
                                "Address successfully updated!",
                                Toast.LENGTH_SHORT
                            ).show()
                            getAddress()
                        }
                        .addOnFailureListener { e ->
                            Log.w(
                                "EditMenuActivity",
                                "Error updating document",
                                e
                            )
                        }

                    dialog.dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()

        if (adapter != null) {
            adapter!!.stopListening()
        }
    }

    private inner class ProductViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(
            view
        ) {

        fun setContent(name: String, image: String, restaurantLatitude: Double, restaurantLongitude: Double) {
            val nameTextView = view.findViewById<TextView>(R.id.restaurantNameTxtView2)
            val imageView = view.findViewById<ImageView>(R.id.restaurantImageView)

            if (image.isNotEmpty()) {
                Picasso.with(context).load(image).into(imageView)
            }

            nameTextView.text = name

            //calculateDistance(name, restaurantLatitude, restaurantLongitude)
        }

        fun categorySelected(name: String) {
            val cardViewCategory = view.findViewById<CardView>(R.id.restaurantCardView)
            cardViewCategory?.setOnClickListener {
                val nameNoWhitespace = name.replace("\\s".toRegex(), "").decapitalize(Locale.ROOT)
                //Toast.makeText(context, nameNoWhitespace, Toast.LENGTH_SHORT).show()
                communicator.passDataCom("restaurant", nameNoWhitespace)
            }
        }

        fun calculateDistance(name: String, restaurantLatitude: Double, restaurantLongitude: Double) {
            var nameNoWhiteSpace = name.replace("\\s".toRegex(), "").decapitalize(Locale.ROOT)
            val docRef = db.collection("users").document(auth?.uid.toString())
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d("FoodRestaurantFrag", "DocumentSnapshot data: ${document.data}")
                        var userLatitude = document.get("latitude")
                        var userLongitude = document.get("longitude")

                        if (userLatitude!= null && userLongitude != null) {

                            var userLatitudeDouble = userLatitude.toString().toDouble()
                            var userLongitudeDouble = userLongitude.toString().toDouble()
                            Log.d("checkingUserLatLng", "$userLatitudeDouble $userLatitude")
                            Log.d("checkingRestLatLng", "$restaurantLatitude $restaurantLongitude")


                            val userLocation = Location("locationA")
                            userLocation.latitude = userLatitudeDouble!!
                            userLocation.longitude = userLongitudeDouble!!

                            val restaurantLocation = Location("locationA")
                            restaurantLocation.latitude = restaurantLatitude
                            restaurantLocation.longitude = restaurantLongitude

                            val distance = userLocation.distanceTo(restaurantLocation).toDouble()
                            val roundedDistance = String.format("%.1f", distance/1000)
                            val distanceTextView = view.findViewById<TextView>(R.id.restaurantDistanceTextView)
                            distanceTextView.text = "${roundedDistance}km away"
                            Log.d("checkingDistance", "$distance")
                        }
                        else {
                            Log.d("FoodRestFrag", "$name lat or long null")
                        }
                    } else {
                        Log.d("FoodRestaurantFrag", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("FoodRestaurantFrag", "get failed with ", exception)
                }

        }


    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<FoodRestaurant>) :
        FirestoreRecyclerAdapter<FoodRestaurant, FoodRestaurantFragment.ProductViewHolder>(
            options
        ) {
        override fun onBindViewHolder(
            productViewHolder: FoodRestaurantFragment.ProductViewHolder,
            position: Int,
            foodRestaurant: FoodRestaurant
        ) {
            productViewHolder.setContent(foodRestaurant.name, foodRestaurant.image, foodRestaurant.latitude, foodRestaurant.longitude)
            productViewHolder.categorySelected(foodRestaurant.name)
            productViewHolder.calculateDistance(foodRestaurant.name, foodRestaurant.latitude, foodRestaurant.longitude)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): FoodRestaurantFragment.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_restaurant,
                parent,
                false
            )
            return ProductViewHolder(view)
        }
    }

}