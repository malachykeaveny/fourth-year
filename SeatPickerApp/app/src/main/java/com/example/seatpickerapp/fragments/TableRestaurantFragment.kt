package com.example.seatpickerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seatpickerapp.R
import com.example.seatpickerapp.dataClasses.FoodRestaurant
import com.example.seatpickerapp.databinding.FragmentTableRestaurantBinding
import com.example.seatpickerapp.interfaces.Communicator
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.*


class TableRestaurantFragment : Fragment() {

    private var adapter: TableRestaurantFragment.ProductFirestoreRecyclerAdapter? = null
    private var auth: FirebaseAuth? = null
    private var _binding: FragmentTableRestaurantBinding?= null
    private val binding get() = _binding!!
    val db = FirebaseFirestore.getInstance()
    private lateinit var communicator: Communicator

    companion object {
        var currentDate: String = ""
        var currentTime: String = ""
        var currentTableNo: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTableRestaurantBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val restaurantRef = db.collection("restaurants")
        binding.chooseRestaurantTableRV.layoutManager = LinearLayoutManager(context)
        val options = FirestoreRecyclerOptions.Builder<FoodRestaurant>().setQuery(restaurantRef, FoodRestaurant::class.java).build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.chooseRestaurantTableRV.adapter = adapter

        communicator = activity as Communicator
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

        fun setContent(name: String, image: String, location: String) {
            val nameTextView = view.findViewById<TextView>(R.id.restaurantNameTxtView2)
            val addressTextView = view.findViewById<TextView>(R.id.restaurantAddressTxtView2)
            val imageView = view.findViewById<ImageView>(R.id.restaurantImageView2)

            if (image.isNotEmpty()) {
                Picasso.with(context).load(image).into(imageView)
            }

            nameTextView.text = name
            addressTextView.text = location
        }

        fun categorySelected(name: String) {
            val cardViewCategory = view.findViewById<CardView>(R.id.restaurantCardView2)
            cardViewCategory.setOnClickListener {
                val nameNoWhitespace = name.replace("\\s".toRegex(), "").decapitalize(Locale.ROOT)
                Toast.makeText(context, nameNoWhitespace, Toast.LENGTH_SHORT).show()
                communicator.passDataCom("tableRestaurant", nameNoWhitespace)
            }
        }


    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<FoodRestaurant>) : FirestoreRecyclerAdapter<FoodRestaurant, TableRestaurantFragment.ProductViewHolder>(options) {
        override fun onBindViewHolder(productViewHolder: TableRestaurantFragment.ProductViewHolder, position: Int, foodRestaurant: FoodRestaurant) {
            productViewHolder.setContent(foodRestaurant.name, foodRestaurant.image, foodRestaurant.location)
            productViewHolder.categorySelected(foodRestaurant.name)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableRestaurantFragment.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_restaurant_v2, parent, false)
            return ProductViewHolder(view)
        }
    }

}