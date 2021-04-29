package com.example.seatpickerapp.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seatpickerapp.R
import com.example.seatpickerapp.activities.TOPIC
import com.example.seatpickerapp.dataClasses.Booking
import com.example.seatpickerapp.databinding.FragmentReportPositiveTestBinding
import com.example.seatpickerapp.firebaseNotifications.NotificationData
import com.example.seatpickerapp.firebaseNotifications.PushNotification
import com.example.seatpickerapp.firebaseNotifications.RetrofitInstance
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.*


class ReportPositiveTestFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    val db = FirebaseFirestore.getInstance()
    private var _binding: FragmentReportPositiveTestBinding? = null
    private val binding get() = _binding!!
    private var adapter: ReportPositiveTestFragment.ProductFirestoreRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_report_positive_test, container, false)
        _binding = FragmentReportPositiveTestBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val bookingCollectionRef = db.collection("users").document(auth?.uid.toString()).collection(
            "booking"
        )
        binding.reportRecyclerView.layoutManager = LinearLayoutManager(context)
        val options = FirestoreRecyclerOptions.Builder<Booking>().setQuery(
            bookingCollectionRef,
            Booking::class.java
        ).build()

        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.reportRecyclerView.adapter = adapter
        adapter!!.startListening()
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
        val db = FirebaseFirestore.getInstance()

        fun setProductName(restaurant: String, date: String, time: String, tableNo: String) {
            val restaurantTextView = view.findViewById<TextView>(R.id.orderRestaurantText)
            val dateTextView = view.findViewById<TextView>(R.id.orderItemsText)
            val timeTextView = view.findViewById<TextView>(R.id.orderDateText)
            val tableNoTextView = view.findViewById<TextView>(R.id.orderTimeText)
            val card_View = view.findViewById<CardView>(R.id.cardViewReport)

            Log.d("DisplayBookingsReport", date)

            restaurantTextView.text = "$restaurant"
            dateTextView.text = "$date"
            timeTextView.text = "$time"

            val deleteButton = view.findViewById<Button>(R.id.bookingDeleteButton)
            val preOrderButton = view.findViewById<Button>(R.id.bookingPreOrderButton)

            deleteButton.visibility = View.GONE
            preOrderButton.visibility = View.GONE

            var tableLong: String? = null
            when (tableNo) {
                "tableOne" -> tableLong = "1"
                "tableTwo" -> tableLong = "2"
                "tableThree" -> tableLong = "3"
                "tableFour" -> tableLong = "4"
                "tableFive" -> tableLong = "5"
                "tableSix" -> tableLong = "6"
                "tableSeven" -> tableLong = "7"
                "tableEight" -> tableLong = "8"
                "tableNine" -> tableLong = "9"
                "tableTen" -> tableLong = "10"
            }
            tableNoTextView.text = "Table $tableLong"

        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun reportPositiveTest(
            restaurant: String, documentId: String, date: String, tableNo: String, time: String
        ) {
            val cVReport = view.findViewById<CardView>(R.id.bookingCardView)
            cVReport.setOnClickListener {
                //Toast.makeText(context, documentId + " " + date + " " + tableNo + " " + time, Toast.LENGTH_SHORT).show()

                val splitDate = date.split(".")
                Log.d("ReportCheckingDate", "${splitDate[0]}")

                val currentDate: LocalDate = LocalDate.now()
                val currentDateMinus6Months: LocalDate = currentDate.minusMonths(6)

                val date1: LocalDate =
                    LocalDate.of(splitDate[2].toInt(), splitDate[1].toInt(), splitDate[0].toInt())
                println("\ndate1 : $date1")
                if (date1.isBefore(currentDate)) {
                    Log.d("ReportCheckingDate", "$date1 is before $currentDate")
                    reportPositiveTestDialog(restaurant, documentId, date, tableNo, time)
                } else {
                    Log.d("ReportCheckingDate", "$date1 is NOT before $currentDate")
                    queryUserDialog(restaurant, documentId, date, tableNo, time)
                }

            }
        }

        private fun queryUserDialog(
            restaurant: String,
            documentId: String,
            date: String,
            tableNo: String,
            time: String
        ) {
            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle("Booking has not yet happened")
            builder.setMessage("You cannot report a positive test for a booking which has not happened. Would you like to cancel the booking?")

            builder.setPositiveButton("Yes") { dialog, which ->
                db.collection("users").document(auth?.uid.toString()).collection("booking")
                    .document(documentId)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("BookingsFragment", "User DocumentSnapshot successfully deleted!")
                        Toast.makeText(context, "Booking deleted!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            "BookingsFragment",
                            "Error deleting document",
                            e
                        )
                    }

                //Toast.makeText(context, documentId + " " + date + " " + tableNo + " " + time, Toast.LENGTH_SHORT).show()

                db.collection("restaurants").document(
                    restaurant.replace("\\s".toRegex(), "")
                        .decapitalize(Locale.ROOT)
                ).collection("tables").document(tableNo).collection(date).document(time)
                    .delete()
                    .addOnSuccessListener {
                        Log.d(
                            "BookingsFragment",
                            "Restaurant DocumentSnapshot successfully deleted!"
                        )
                        Toast.makeText(context, "Booking deleted!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            "BookingsFragment",
                            "Error deleting restaurant document",
                            e
                        )
                    }

                val adminCollectionRef =
                    db.collection("restaurants").document("flanagans").collection("bookingsMgmt")
                        .document("tableBookings").collection(date)
                val adminRef =
                    adminCollectionRef.whereEqualTo("time", time).whereEqualTo("tableNo", tableNo)
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

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(context, android.R.string.no, Toast.LENGTH_SHORT).show()
            }

            builder.show()

        }

        private fun reportPositiveTestDialog(
            restaurant: String,
            documentId: String,
            date: String,
            tableNo: String,
            time: String
        ) {
            var tableLong: String? = null
            when (tableNo) {
                "tableOne" -> tableLong = "1"
                "tableTwo" -> tableLong = "2"
                "tableThree" -> tableLong = "3"
                "tableFour" -> tableLong = "4"
                "tableFive" -> tableLong = "5"
                "tableSix" -> tableLong = "6"
                "tableSeven" -> tableLong = "7"
                "tableEight" -> tableLong = "8"
                "tableNine" -> tableLong = "9"
                "tableTen" -> tableLong = "10"
            }

            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle("Confirm Positive Covid-19 Test")
            builder.setMessage("Date: $date\nTime: $time\nTable Number: $tableLong")
            //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton("Yes") { dialog, which ->
                Toast.makeText(
                    context,
                    "Thank you for reporting your positive covid-19 test, we will notify the applicable guests and staff. Get well soon.",
                    Toast.LENGTH_SHORT
                ).show()
                var myToken: String? = null
                Log.d("reportRestaurant", restaurant)

                lifecycleScope.launch {
                    try {
                        val tablesList = mutableListOf(
                            "tableOne",
                            "tableTwo",
                            "tableThree",
                            "tableFour",
                            "tableFive",
                            "tableSix",
                            "tableSeven",
                            "tableEight",
                            "tableNine",
                            "tableTen"
                        )
                        for (i in tablesList) {
                            val tableCollectionRef = db.collection("restaurants").document(
                                restaurant
                            ).collection("tables").document(i).collection(date)
                            val tableSnapshot = tableCollectionRef.get().await()
                            for (doc in tableSnapshot.documents) {
                                if (doc.get("time") == time) {
                                    Log.d("reportTokenCheck", doc.get("token").toString())
                                    FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                                    val title = "Covid-19 alert"
                                    val message =
                                        "You are being notified that a positive Covid-19 case has been reported from a neaby table at your recent visit at $restaurant's on $date at $time"
                                    val recipientToken = doc.get("token").toString()
                                    PushNotification(
                                        NotificationData(title, message),
                                        recipientToken
                                    ).also {
                                        sendNotification(it)
                                    }
                                }
                            }
                        }

                    } catch (e: Exception) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(context, android.R.string.no, Toast.LENGTH_SHORT).show()
            }

            builder.show()
        }

        private fun sendNotification(notification: PushNotification) =
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitInstance.api.postNotification(notification)
                    if (response.isSuccessful) {
                        Log.d("ReportFragment", "Response; ${Gson().toJson(response)}")
                    } else {
                        Log.e("ReportFragment", response.errorBody().toString())
                    }
                } catch (e: Exception) {
                    Log.e("ReportFragment", e.toString())
                }
            }


    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Booking>) :
        FirestoreRecyclerAdapter<Booking, ReportPositiveTestFragment.ProductViewHolder>(
            options
        ) {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(
            productViewHolder: ReportPositiveTestFragment.ProductViewHolder,
            position: Int,
            booking: Booking
        ) {
            productViewHolder.setProductName(
                booking.restaurant.replace("\\s".toRegex(), "").decapitalize(
                    Locale.ROOT
                ), booking.date, booking.time, booking.tableNo
            )

            productViewHolder.reportPositiveTest(
                booking.restaurant.replace("\\s".toRegex(), "").decapitalize(
                    Locale.ROOT
                ), snapshots.getSnapshot(position).id, booking.date, booking.tableNo, booking.time
            )
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ReportPositiveTestFragment.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_booking_v2,
                parent,
                false
            )
            return ProductViewHolder(view)
        }
    }


}