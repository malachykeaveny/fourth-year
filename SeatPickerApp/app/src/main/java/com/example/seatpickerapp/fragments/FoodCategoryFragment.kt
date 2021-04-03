package com.example.seatpickerapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seatpickerapp.R
import com.example.seatpickerapp.activities.OrderFoodActivity
import com.example.seatpickerapp.dataClasses.FoodCategory
import com.example.seatpickerapp.databinding.FragmentFoodCategoryBinding
import com.example.seatpickerapp.interfaces.Communicator
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.*


class FoodCategoryFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    private var adapter: FoodCategoryFragment.ProductFirestoreRecyclerAdapter? = null
    val db = FirebaseFirestore.getInstance()
    private var _binding: FragmentFoodCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var communicator: Communicator
    var displayMessage: String?= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodCategoryBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        //displayMessage = arguments?.getString("restaurant")
        Log.d("FoodCategoryFragment", displayMessage.toString())

        val categoryRef = db.collection("restaurants").document(OrderFoodActivity.currentRestaurant).collection("menu")
        binding.categoryRV.layoutManager = GridLayoutManager(context, 2)
        val options = FirestoreRecyclerOptions.Builder<FoodCategory>().setQuery(categoryRef, FoodCategory::class.java).build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.categoryRV.adapter = adapter
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

        fun setContent(name: String, img: String) {
            val nameTextView = view.findViewById<TextView>(R.id.txtViewCategory)
            val imageView = view.findViewById<ImageView>(R.id.imageCategory)

            if (img.isNotEmpty()) {
                Picasso.with(context).load(img).into(imageView)
            }

            nameTextView.text = name
        }

        fun categorySelected(name: String) {
            val cardViewCategory = view.findViewById<CardView>(R.id.cardViewCategory)
            cardViewCategory.setOnClickListener {
                //Toast.makeText(context, name, Toast.LENGTH_SHORT).show()
                val nameNoWhitespace = name.replace("\\s".toRegex(), "").decapitalize(Locale.ROOT)
                communicator.passDataCom("category", nameNoWhitespace)
            }
        }


    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<FoodCategory>) : FirestoreRecyclerAdapter<FoodCategory, FoodCategoryFragment.ProductViewHolder>(options) {
        override fun onBindViewHolder(productViewHolder: FoodCategoryFragment.ProductViewHolder, position: Int, foodCategory: FoodCategory) {
            productViewHolder.setContent(foodCategory.name, foodCategory.image)
            productViewHolder.categorySelected(foodCategory.name)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodCategoryFragment.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_category, parent, false)
            return ProductViewHolder(view)
        }
    }


}