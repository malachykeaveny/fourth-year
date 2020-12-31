package com.example.kotlinpractice

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinpractice.databinding.ActivityDisplayBookingsBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_booking.*

class DisplayBookings : AppCompatActivity() {

    private lateinit var binding: ActivityDisplayBookingsBinding
    private var adapter: DisplayBookings.ProductFirestoreRecyclerAdapter? = null
    val db = FirebaseFirestore.getInstance()
    val bookingCollectionRef = db.collection("users").document("Uykv5wvCCEcuIARFQ6hx").collection("booking")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayBookingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.bookingRecyclerView.layoutManager = LinearLayoutManager(this)
        val options = FirestoreRecyclerOptions.Builder<Booking>().setQuery(bookingCollectionRef, Booking::class.java).build()

        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.bookingRecyclerView.adapter = adapter
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
        val db = FirebaseFirestore.getInstance()

        fun setProductName(date: String, time: String, tableNo: String) {
            val dateTextView = view.findViewById<TextView>(R.id.booking_date_text_view)
            val timeTextView = view.findViewById<TextView>(R.id.booking_time_text_view)
            val tableNoTextView = view.findViewById<TextView>(R.id.booking_tableNo_text_view)
            val card_View = view.findViewById<CardView>(R.id.cardView)

            Log.d("DisplayBookings", date)

            dateTextView.text = date
            timeTextView.text = time
            tableNoTextView.text = tableNo

        }
    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Booking>) : FirestoreRecyclerAdapter<Booking, DisplayBookings.ProductViewHolder>(options) {
        override fun onBindViewHolder(productViewHolder: DisplayBookings.ProductViewHolder, position: Int, booking: Booking) {
            productViewHolder.setProductName(booking.date, booking.time, booking.tabelNo)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisplayBookings.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
            return ProductViewHolder(view)
        }
    }

}