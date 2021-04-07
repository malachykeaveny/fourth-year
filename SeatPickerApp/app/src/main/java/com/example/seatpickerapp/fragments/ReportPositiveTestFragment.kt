package com.example.seatpickerapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seatpickerapp.dataClasses.Booking
import com.example.seatpickerapp.R
import com.example.seatpickerapp.activities.TOPIC
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
import java.lang.Exception


class ReportPositiveTestFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    val db = FirebaseFirestore.getInstance()
    private var _binding: FragmentReportPositiveTestBinding?= null
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

        val bookingCollectionRef = db.collection("users").document(auth?.uid.toString()).collection("booking")
        binding.reportRecyclerView.layoutManager = LinearLayoutManager(context)
        val options = FirestoreRecyclerOptions.Builder<Booking>().setQuery(bookingCollectionRef, Booking::class.java).build()

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

    private inner class ProductViewHolder internal constructor(private val view: View) : RecyclerView.ViewHolder(view) {
        val db = FirebaseFirestore.getInstance()

        fun setProductName(restaurant: String, date: String, time: String, tableNo: String) {
            val restaurantTextView = view.findViewById<TextView>(R.id.report_restaurant_text_view)
            val dateTextView = view.findViewById<TextView>(R.id.report_date_text_view)
            val timeTextView = view.findViewById<TextView>(R.id.report_time_text_view)
            val tableNoTextView = view.findViewById<TextView>(R.id.report_tableNo_text_view)
            val card_View = view.findViewById<CardView>(R.id.cardViewReport)

            Log.d("DisplayBookingsReport", date)

            restaurantTextView.text = "Restaurant: $restaurant"
            dateTextView.text = "Date: $date"
            timeTextView.text = "Time $time"

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
            tableNoTextView.text = "Table Number: $tableLong"

        }

        fun reportPositiveTest(documentId: String, date: String, tableNo: String, time: String) {
            val cVReport = view.findViewById<CardView>(R.id.cardViewReport)
            cVReport.setOnClickListener {
                //Toast.makeText(context, documentId + " " + date + " " + tableNo + " " + time, Toast.LENGTH_SHORT).show()

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

                val builder = AlertDialog.Builder(activity!!)
                builder.setTitle("Confirm Positive Covid-19 Test")
                builder.setMessage("Date: $date\nTime: $time\nTable Number: $tableLong")
                //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

                builder.setPositiveButton("Yes") { dialog, which ->
                    Toast.makeText(context, "Thank you for reporting your positive covid-19 test, we will notify the applicable guests and staff. Get well soon.", Toast.LENGTH_SHORT).show()
                    var myToken: String?= null

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
                                "tableEight"
                            )
                            for (i in tablesList) {
                                val tableCollectionRef =
                                    db.collection("restaurants").document("flanagans")
                                        .collection("tables").document(i).collection(date)
                                val tableSnapshot = tableCollectionRef.get().await()
                                for (doc in tableSnapshot.documents) {
                                    Log.d("reportTokenCheck", doc.get("token").toString())
                                    FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                                    val title = "Covid-19 alert"
                                    val message = "You are being notified that a positive Covid-19 case has been reported from your recent visit at Flannery's on $date at $time"
                                    val recipientToken = doc.get("token").toString()
                                    PushNotification(
                                        NotificationData(title, message),
                                        recipientToken
                                    ).also {
                                        sendNotification(it)
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
        }

        private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.d("ReportFragment", "Response; ${Gson().toJson(response)}")
                }
                else {
                    Log.e("ReportFragment", response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e("ReportFragment", e.toString())
            }
        }


    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Booking>) : FirestoreRecyclerAdapter<Booking, ReportPositiveTestFragment.ProductViewHolder>(options) {
        override fun onBindViewHolder(productViewHolder: ReportPositiveTestFragment.ProductViewHolder, position: Int, booking: Booking) {
            productViewHolder.setProductName(booking.restaurant, booking.date, booking.time, booking.tableNo)

            productViewHolder.reportPositiveTest(snapshots.getSnapshot(position).id, booking.date, booking.tableNo, booking.time)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportPositiveTestFragment.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report_booking, parent, false)
            return ProductViewHolder(view)
        }
    }


}