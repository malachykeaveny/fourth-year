package com.example.seatpickerapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seatpickerapp.Booking
import com.example.seatpickerapp.R
import com.example.seatpickerapp.ReservationActivity
import com.example.seatpickerapp.databinding.FragmentBookingsBinding
import com.example.seatpickerapp.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth


class BookingsFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    private var adapter: BookingsFragment.ProductFirestoreRecyclerAdapter? = null
    val db = FirebaseFirestore.getInstance()
    val bookingCollectionRef = db.collection("users").document("4QM8lSsAaTgX0YUC0DtV7ETSgBy2").collection("booking")
    private var _binding: FragmentBookingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookingsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.bookingsRecyclerView.layoutManager = LinearLayoutManager(context)
        val options = FirestoreRecyclerOptions.Builder<Booking>().setQuery(bookingCollectionRef, Booking::class.java).build()

        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.bookingsRecyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
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
        val db = FirebaseFirestore.getInstance()

        fun setProductName(date: String, time: String, tableNo: String) {
            val dateTextView = view.findViewById<TextView>(R.id.booking_date_text_view)
            val timeTextView = view.findViewById<TextView>(R.id.booking_time_text_view)
            val tableNoTextView = view.findViewById<TextView>(R.id.booking_tableNo_text_view)
            val card_View = view.findViewById<CardView>(R.id.cardViewBooking)

            Log.d("DisplayBookings", date)

            dateTextView.text = date
            timeTextView.text = time
            tableNoTextView.text = tableNo

        }

        fun deleteBooking(documentId: String, date: String, tableNo: String, time: String) {
            val delete_btn = view.findViewById<FloatingActionButton>(R.id.fl_btn_delete)
            delete_btn.setOnClickListener {

                db.collection("users").document(auth?.uid.toString()).collection("booking").document(documentId)
                    .delete()
                    .addOnSuccessListener { Log.d("BookingsFragment", "User DocumentSnapshot successfully deleted!")
                        Toast.makeText(context, "Booking deleted!", Toast.LENGTH_SHORT).show()}
                    .addOnFailureListener { e -> Log.w("BookingsFragment", "Error deleting document", e) }

                //Toast.makeText(context, documentId + " " + date + " " + tableNo + " " + time, Toast.LENGTH_SHORT).show()

                db.collection("restaurants").document("flanagans").collection("tables").document(tableNo).collection(date).document(time)
                    .delete()
                    .addOnSuccessListener { Log.d("BookingsFragment", "Restaurant DocumentSnapshot successfully deleted!")
                        Toast.makeText(context, "Booking deleted!", Toast.LENGTH_SHORT).show()}
                    .addOnFailureListener { e -> Log.w("BookingsFragment", "Error deleting restaurant document", e) }
            }
        }
    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Booking>) : FirestoreRecyclerAdapter<Booking, BookingsFragment.ProductViewHolder>(options) {
        override fun onBindViewHolder(productViewHolder: BookingsFragment.ProductViewHolder, position: Int, booking: Booking) {
            productViewHolder.setProductName(booking.date, booking.time, booking.tableNo)

            productViewHolder.deleteBooking(snapshots.getSnapshot(position).id, booking.date, booking.tableNo, booking.time)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingsFragment.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
            return ProductViewHolder(view)
        }
    }



}