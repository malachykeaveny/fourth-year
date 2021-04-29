    package com.example.seatpickerapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seatpickerapp.dataClasses.Booking
import com.example.seatpickerapp.R
import com.example.seatpickerapp.activities.PreOrderMealActivity
import com.example.seatpickerapp.databinding.FragmentBookingsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import java.util.*


    class BookingsFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    private var adapter: BookingsFragment.ProductFirestoreRecyclerAdapter? = null
    val db = FirebaseFirestore.getInstance()
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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val bookingCollectionRef = db.collection("users").document(auth?.uid.toString()).collection("booking").orderBy("date", Query.Direction.ASCENDING)
        binding.bookingsRecyclerView.layoutManager = LinearLayoutManager(context)
        //binding.bookingsRecyclerView.layoutManager = GridLayoutManager(context, 2)
        val options = FirestoreRecyclerOptions.Builder<Booking>().setQuery(bookingCollectionRef, Booking::class.java).build()

        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.bookingsRecyclerView.adapter = adapter
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

        fun setProductName(restaurant: String, date: String, time: String, tableNo: String) {
            val restaurantTextView = view.findViewById<TextView>(R.id.orderRestaurantText)
            val dateTextView = view.findViewById<TextView>(R.id.orderItemsText)
            val timeTextView = view.findViewById<TextView>(R.id.orderDateText)
            val tableNoTextView = view.findViewById<TextView>(R.id.orderTimeText)
            val card_View = view.findViewById<CardView>(R.id.bookingCardView)

            Log.d("DisplayBookings", date)

            restaurantTextView.text = "$restaurant"
            dateTextView.text = "$date"
            timeTextView.text = "$time"

            var tableLong: String?= null
            when (tableNo) {
                "tableOne" -> tableLong = "1"
                "tableTwo" -> tableLong = "2"
                "tableThree" -> tableLong = "3"
                "tableFour" -> tableLong = "4"
                "tableFive" -> tableLong = "5"
                "tableSix" -> tableLong = "6"
                "tableSeven" -> tableLong = "7"
                "tableEight" -> tableLong = "8"
            }
            tableNoTextView.text = "Table $tableLong"

        }

        fun deleteBooking(documentId: String, restaurant: String, date: String, tableNo: String, time: String) {
            val delete_btn = view.findViewById<Button>(R.id.bookingDeleteButton)
            delete_btn.setOnClickListener {

                val builder = AlertDialog.Builder(requireActivity())
                builder.setTitle("Cancel booking")
                var tableLong: String? = null

                builder.setMessage("Are you sure you want to cancel this booking?")
                //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

                builder.setPositiveButton("Yes") { dialog, which ->
                    db.collection("users").document(auth?.uid.toString()).collection("booking").document(documentId)
                        .delete()
                        .addOnSuccessListener { Log.d("BookingsFragment", "User DocumentSnapshot successfully deleted!")
                            Toast.makeText(context, "Booking deleted!", Toast.LENGTH_SHORT).show()}
                        .addOnFailureListener { e -> Log.w("BookingsFragment", "Error deleting document", e) }

                    //Toast.makeText(context, documentId + " " + date + " " + tableNo + " " + time, Toast.LENGTH_SHORT).show()

                    db.collection("restaurants").document(restaurant.replace("\\s".toRegex(), "")
                        .decapitalize(Locale.ROOT)).collection("tables").document(tableNo).collection(date).document(time)
                        .delete()
                        .addOnSuccessListener { Log.d("BookingsFragment", "Restaurant DocumentSnapshot successfully deleted!")
                            Toast.makeText(context, "Booking deleted!", Toast.LENGTH_SHORT).show()}
                        .addOnFailureListener { e -> Log.w("BookingsFragment", "Error deleting restaurant document", e) }

                    val adminCollectionRef = db.collection("restaurants").document("flanagans").collection("bookingsMgmt").document("tableBookings").collection(date)
                    val adminRef = adminCollectionRef.whereEqualTo("time", time).whereEqualTo("tableNo", tableNo)
                    adminRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                adminCollectionRef.document(document.id).delete()
                            }
                        } else {
                            Log.d("BookingsFragmentadmin", "Error getting documents: ", task.exception)
                        }
                    }

                }
                builder.setNegativeButton("No") { dialog, which ->
                    //Toast.makeText(context, android.R.string.no, Toast.LENGTH_SHORT).show()
                }

                builder.show()
                }

        }

        fun preOrder(id: String, restaurant: String, date: String, tableNo: String, time: String) {
            val preOrderFloatingActionButton = view.findViewById<Button>(R.id.bookingPreOrderButton)

            preOrderFloatingActionButton.setOnClickListener {
                Log.d("BookingsFragment", "$restaurant $date $tableNo $time")
                TableRestaurantFragment.currentDate = date
                TableRestaurantFragment.currentTime = time
                TableRestaurantFragment.currentTableNo = tableNo
                FlanagansFragment.preOrderRestaurant = restaurant

                startActivity(Intent(context, PreOrderMealActivity::class.java))
            }
        }
    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Booking>) : FirestoreRecyclerAdapter<Booking, BookingsFragment.ProductViewHolder>(options) {
        override fun onBindViewHolder(productViewHolder: BookingsFragment.ProductViewHolder, position: Int, booking: Booking) {
            productViewHolder.setProductName(booking.restaurant, booking.date, booking.time, booking.tableNo)
            productViewHolder.deleteBooking(snapshots.getSnapshot(position).id, booking.restaurant, booking.date, booking.tableNo, booking.time)
            productViewHolder.preOrder(snapshots.getSnapshot(position).id, booking.restaurant, booking.date, booking.tableNo, booking.time)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingsFragment.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking_v2, parent, false)
            return ProductViewHolder(view)
        }
    }



}