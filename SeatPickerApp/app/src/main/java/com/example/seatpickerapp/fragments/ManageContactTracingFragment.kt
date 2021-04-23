package com.example.seatpickerapp.fragments

import android.app.DatePickerDialog
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
import com.example.seatpickerapp.R
import com.example.seatpickerapp.activities.AdminHomeActivity
import com.example.seatpickerapp.activities.TOPIC
import com.example.seatpickerapp.dataClasses.BookingMgmt
import com.example.seatpickerapp.databinding.FragmentManageContactTracingBinding
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
import java.util.*


class ManageContactTracingFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    val db = FirebaseFirestore.getInstance()
    private var _binding: FragmentManageContactTracingBinding?= null
    private val binding get() = _binding!!
    private var adapter: ManageContactTracingFragment.ProductFirestoreRecyclerAdapter? = null
    private var date: String? = null
    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    private var adminCurrentRestaurant: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentManageContactTracingBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        adminCurrentRestaurant = AdminHomeActivity.adminRestaurantName.replace("\\s".toRegex(), "").decapitalize(Locale.ROOT)
        Log.d("ManageContactTracingFr", adminCurrentRestaurant!!)

        binding.managerCFDateButton.setOnClickListener {
            setDate()
        }

        val bookingCollectionRef = db.collection("restaurants").document(adminCurrentRestaurant).collection("bookingsMgmt").document("tableBookings").collection(date.toString())
        binding.managerCFRV.layoutManager = LinearLayoutManager(context)
        val options = FirestoreRecyclerOptions.Builder<BookingMgmt>().setQuery(bookingCollectionRef, BookingMgmt::class.java).build()

        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.managerCFRV.adapter = adapter
    }

    private fun setDate() {
        val dpd =
            DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                var month = mMonth + 1
                date = "$mDay.$month.$mYear"
                binding.managerCFDateButton.setText(date)
                //getAvailableTimes(date!!)
                setupRecyclerView(date)
            }, year, month, day)
        //dpd.datePicker.minDate = System.currentTimeMillis() - 1000
        dpd.show()
    }

    private fun setupRecyclerView(date: String?) {
        adapter?.stopListening()
        val bookingCollectionRef = db.collection("restaurants").document(adminCurrentRestaurant).collection("bookingsMgmt").document("tableBookings").collection(date.toString())
        binding.managerCFRV.layoutManager = LinearLayoutManager(context)
        val options = FirestoreRecyclerOptions.Builder<BookingMgmt>()
            .setQuery(bookingCollectionRef, BookingMgmt::class.java).build()

        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.managerCFRV.adapter = adapter
        adapter!!.startListening()
        //adapter!!.notifyDataSetChanged()
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

        fun setUpRecyclerView(name: String, date: String, time: String, tableNo: String, phoneNo: String, email: String) {
            val nameTextView = view.findViewById<TextView>(R.id.booking_mgmt_name_text_view)
            val dateTextView = view.findViewById<TextView>(R.id.booking_mgmt_date_text_view)
            val timeTextView = view.findViewById<TextView>(R.id.booking_mgmt_time_text_view)
            val tableNoTextView = view.findViewById<TextView>(R.id.booking_mgmt_tableNo_text_view)
            val emailTextView = view.findViewById<TextView>(R.id.booking_mgmt_email_text_view)
            val phoneNoTextView = view.findViewById<TextView>(R.id.booking_mgmt_phoneNo_text_view)

            nameTextView.text = "Name: $name"
            dateTextView.text = "Date: $date"
            timeTextView.text = "Time: $time"
            tableNoTextView.text = "Table number: $tableNo"
            emailTextView.text = "Email: $email"
            phoneNoTextView.text = "Phone number: $phoneNo"
            Log.d("ManageBookings", name)
        }

        fun manageBooking(id: String, name: String, date: String, time: String, tableNo: String, phoneNo: String, email: String) {
            val cVManage = view.findViewById<CardView>(R.id.cardViewBookingMgmt)

            cVManage.setOnClickListener {

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
                    "tableNine" -> tableLong = "9"
                }

                val builder = AlertDialog.Builder(activity!!)
                builder.setTitle("Confirm Positive Covid-19 Test")
                builder.setMessage("Date: $date\nTime: $time\nTable Number: $tableLong")

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
                                "tableEight",
                                "tableNine",
                                "tableTen"
                            )
                            for (i in tablesList) {
                                val tableCollectionRef =
                                    db.collection("restaurants").document(adminCurrentRestaurant)
                                        .collection("tables").document(i).collection(date)
                                val tableSnapshot = tableCollectionRef.get().await()
                                for (doc in tableSnapshot.documents) {
                                    Log.d("reportTokenCheck", doc.get("token").toString())
                                    FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                                    val title = "Covid-19 alert"
                                    val message = "You are being notified that a positive Covid-19 case has been reported from your recent visit at $adminCurrentRestaurant on $date at $time"
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

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<BookingMgmt>) : FirestoreRecyclerAdapter<BookingMgmt, ProductViewHolder>(options) {
        override fun onBindViewHolder(productViewHolder: ProductViewHolder, position: Int, bookingMgmt: BookingMgmt) {

            productViewHolder.setUpRecyclerView(bookingMgmt.name, bookingMgmt.date, bookingMgmt.time, bookingMgmt.tableNo, bookingMgmt.phoneNo, bookingMgmt.email)

            productViewHolder.manageBooking(snapshots.getSnapshot(position).id, bookingMgmt.name, bookingMgmt.date, bookingMgmt.time, bookingMgmt.tableNo, bookingMgmt.phoneNo, bookingMgmt.email)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking_mgmt, parent, false)
            return ProductViewHolder(view)
        }
    }

}