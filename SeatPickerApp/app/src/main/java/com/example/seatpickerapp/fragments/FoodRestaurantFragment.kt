package com.example.seatpickerapp.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seatpickerapp.R
import com.example.seatpickerapp.dataClasses.FoodCategory
import com.example.seatpickerapp.dataClasses.FoodRestaurant
import com.example.seatpickerapp.databinding.FragmentFoodRestaurantBinding
import com.example.seatpickerapp.interfaces.Communicator
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*

class FoodRestaurantFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    private var adapter: FoodRestaurantFragment.ProductFirestoreRecyclerAdapter? = null
    private var _binding: FragmentFoodRestaurantBinding?= null
    private val binding get() = _binding!!
    val db = FirebaseFirestore.getInstance()
    private lateinit var communicator: Communicator
    private lateinit  var locationManager: LocationManager
    private var userHasGPS = false
    private var userHasNetwork = false

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
        val options = FirestoreRecyclerOptions.Builder<FoodRestaurant>().setQuery(restaurantRef, FoodRestaurant::class.java).build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.restaurantRV.adapter = adapter
        communicator = activity as Communicator

        getAddress()

        binding.setUserAddressButton.setOnClickListener {
            addAddress()
        }

        binding.getLocationButton.setOnClickListener {

        }
    }

    private fun getLocation() {
        //locationManager = getSystemService(requireContext().applicationContext.L) as LocationManager
    }


    private fun getAddress() {
        lifecycleScope.launch {
            try {
                val userCollectionRef = Firebase.firestore.collection("users")
                val querySnapshot = userCollectionRef.get().await()
                for (document in querySnapshot.documents) {
                    //Log.d("FoodRestaurantFragment", document.id + " " + auth?.uid.toString())
                    if (document.id == auth?.uid.toString()) {
                        val addressLine1 = document.get("addressLine1").toString()
                        val addressEircode = document.get("addressEircode").toString()
                        Log.d("FoodRestaurantFragment", "$addressLine1 $addressEircode")
                        binding.userAddressTxtView.text = "$addressLine1, $addressEircode"
                    }
                }
            }
            catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
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
                            Log.d("FoodRestaurantFragment", "DocumentSnapshot successfully updated!")
                            Toast.makeText(requireContext(), "Address successfully updated!", Toast.LENGTH_SHORT).show()
                            getAddress()
                        }
                        .addOnFailureListener { e -> Log.w("EditMenuActivity", "Error updating document", e) }

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

    private inner class ProductViewHolder internal constructor(private val view: View) : RecyclerView.ViewHolder(view) {

        fun setContent(name: String, image: String) {
            val nameTextView = view.findViewById<TextView>(R.id.txtViewCategory)
            val imageView = view.findViewById<ImageView>(R.id.imageCategory)

            if (image.isNotEmpty()) {
                Picasso.with(context).load(image).into(imageView)
            }

            nameTextView.text = name
        }

        fun categorySelected(name: String) {
            val cardViewCategory = view.findViewById<CardView>(R.id.cardViewCategory)
            cardViewCategory.setOnClickListener {
                val nameNoWhitespace = name.replace("\\s".toRegex(), "").decapitalize(Locale.ROOT)
                //Toast.makeText(context, nameNoWhitespace, Toast.LENGTH_SHORT).show()
                communicator.passDataCom("restaurant", nameNoWhitespace)
            }
        }


    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<FoodRestaurant>) : FirestoreRecyclerAdapter<FoodRestaurant, FoodRestaurantFragment.ProductViewHolder>(options) {
        override fun onBindViewHolder(productViewHolder: FoodRestaurantFragment.ProductViewHolder, position: Int, foodRestaurant: FoodRestaurant) {
            productViewHolder.setContent(foodRestaurant.name, foodRestaurant.image)
            productViewHolder.categorySelected(foodRestaurant.name)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodRestaurantFragment.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_category, parent, false)
            return ProductViewHolder(view)
        }
    }

}