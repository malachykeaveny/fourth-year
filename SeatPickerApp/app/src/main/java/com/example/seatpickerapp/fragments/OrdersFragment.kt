package com.example.seatpickerapp.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seatpickerapp.R
import com.example.seatpickerapp.activities.OrderFoodActivity
import com.example.seatpickerapp.dataClasses.Booking
import com.example.seatpickerapp.dataClasses.FoodCategory
import com.example.seatpickerapp.dataClasses.Order
import com.example.seatpickerapp.databinding.FragmentOrdersBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class OrdersFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    private var _binding: FragmentOrdersBinding?= null
    private val binding get() = _binding!!
    val db = FirebaseFirestore.getInstance()
    private var adapter: OrdersFragment.ProductFirestoreRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()

        val orderCollectionRef = db.collection("users").document(auth?.uid.toString()).collection("order")
        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(context)
        val options = FirestoreRecyclerOptions.Builder<Order>().setQuery(orderCollectionRef, Order::class.java).build()

        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.ordersRecyclerView.adapter = adapter
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

        fun setContent(restaurant: String, items: String, date: String, time: String, address: String) {
            val restaurantTxtView = view.findViewById<TextView>(R.id.txtViewRestaurant)
            val itemsTextView = view.findViewById<TextView>(R.id.txtViewItems)
            val dateTextView = view.findViewById<TextView>(R.id.orderDateTextView)
            val timeTextView = view.findViewById<TextView>(R.id.orderTimeTextView)
            val addressTextView = view.findViewById<TextView>(R.id.orderAddressTextView)

            itemsTextView.text = items.dropLast(1)
            dateTextView.text = date
            timeTextView.text = time
            addressTextView.text = address

            val docRef = db.collection("restaurants").document(restaurant)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d("OrdersFragment", "DocumentSnapshot data: ${document.get("name")}")
                        restaurantTxtView.text = document.get("name").toString()
                    } else {
                        Log.d("OrdersFragment", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("OrdersFragment", "get failed with ", exception)
                }
        }


    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Order>) : FirestoreRecyclerAdapter<Order, OrdersFragment.ProductViewHolder>(options) {
        override fun onBindViewHolder(productViewHolder: OrdersFragment.ProductViewHolder, position: Int, order: Order) {
            productViewHolder.setContent(order.restaurant, order.items, order.date, order.time, order.address)
           // productViewHolder.categorySelected(foodCategory.name)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersFragment.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
            return ProductViewHolder(view)
        }
    }

}