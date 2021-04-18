package com.example.softwarepatternsca2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.softwarepatternsca2.databinding.ActivityUserOrdersBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserOrdersBinding
    private var auth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance()
    private var adapter: UserOrdersActivity.ProductFirestoreRecyclerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserOrdersBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        val userCollectionRef = db.collection("users").document(auth?.uid.toString()).collection("order")
        binding.userOrdersRV.layoutManager = LinearLayoutManager(applicationContext)
        val options = FirestoreRecyclerOptions.Builder<Order>().setQuery(userCollectionRef, Order::class.java).build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.userOrdersRV.adapter = adapter
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
        RecyclerView.ViewHolder(view) {

        fun setContent(address: String, date: String, items: String, time: String) {
            val itemsTextView = view.findViewById<TextView>(R.id.itemsTextView)
            val dateTextView = view.findViewById<TextView>(R.id.dateTextView)
            val timeTextView = view.findViewById<TextView>(R.id.timeTextView)
            val addressTextView = view.findViewById<TextView>(R.id.addressTextView)

            itemsTextView.text = items
            dateTextView.text = date
            timeTextView.text = time
            addressTextView.text = address

            Log.d("checkingOrder", "$address $date $items $time")
        }

    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Order>) : FirestoreRecyclerAdapter<Order, ProductViewHolder>(options) {

        override fun onBindViewHolder(productViewHolder: UserOrdersActivity.ProductViewHolder, position: Int, order: Order) {
            productViewHolder.setContent(order.address, order.date, order.items, order.time)
            //productViewHolder.itemSelected(item.itemName, item.price, item.image, item.stock)
            //productViewHolder.deleteItem(snapshots.getSnapshot(position).id)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): UserOrdersActivity.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
            return ProductViewHolder(view)
        }
    }
}