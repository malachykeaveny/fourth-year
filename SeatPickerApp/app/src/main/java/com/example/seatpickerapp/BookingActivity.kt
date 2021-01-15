package com.example.seatpickerapp

import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.seatpickerapp.databinding.ActivityBookingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding
    private val TAG = "TableLayoutMessing"
    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    val db = FirebaseFirestore.getInstance()
    private var date: String? = null
    private var time: String? = null
    private val personCollectionRef = Firebase.firestore.collection("users")
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        binding.bookingScrollViewHoriz.setBackgroundResource(R.drawable.ic_floor_plan)

        binding.selectDateBtn.setOnClickListener {
            setDate()
        }

        binding.ivTableOne.setOnClickListener {
            binding.ivTableOne.setImageResource(R.drawable.ic_table_reserved_big)
        }

        binding.twoPmBtn.setOnClickListener {
            //getAvailableTimes(date!!, "14.00")
            getAvailableTables(date!!, "14.00")
            time = "14.00"
        }

        binding.fourPmBtn.setOnClickListener {
            getAvailableTables(date!!, "16.00")
            time = "16.00"
        }

        binding.sixPmBtn.setOnClickListener {
            getAvailableTables(date!!, "18.00")
            time = "18.00"
        }

        binding.eightPmBtn.setOnClickListener {
            getAvailableTables(date!!, "20.00")
            time = "20.00"
        }

        binding.ivTableOne.setOnClickListener {

            if (date == null || time == null) {
                Toast.makeText(this@BookingActivity, "Pick a date or a time", Toast.LENGTH_SHORT)
                    .show()
            } else {
                bookingDialog(date!!, time!!, "tableOne")
            }
        }

        binding.ivTableTwo.setOnClickListener {

            if (date == null || time == null) {
                Toast.makeText(this@BookingActivity, "Pick a date or a time", Toast.LENGTH_SHORT)
                    .show()
            } else {
                bookingDialog(date!!, time!!, "tableTwo")
            }
        }

        binding.ivTableThree.setOnClickListener {

            if (date == null || time == null) {
                Toast.makeText(this@BookingActivity, "Pick a date or a time", Toast.LENGTH_SHORT)
                    .show()
            } else {
                bookingDialog(date!!, time!!, "tableThree")
            }
        }

        binding.ivTableFour.setOnClickListener {

            if (date == null || time == null) {
                Toast.makeText(this@BookingActivity, "Pick a date or a time", Toast.LENGTH_SHORT)
                    .show()
            } else {
                bookingDialog(date!!, time!!, "tableFour")
            }
        }

        binding.ivTableFive.setOnClickListener {

            if (date == null || time == null) {
                Toast.makeText(this@BookingActivity, "Pick a date or a time", Toast.LENGTH_SHORT)
                    .show()
            } else {
                bookingDialog(date!!, time!!, "tableFive")
            }
        }

        binding.ivTableSix.setOnClickListener {

            if (date == null || time == null) {
                Toast.makeText(this@BookingActivity, "Pick a date or a time", Toast.LENGTH_SHORT)
                    .show()
            } else {
                bookingDialog(date!!, time!!, "tableSix")
            }
        }

        binding.ivTableSeven.setOnClickListener {

            if (date == null || time == null) {
                Toast.makeText(this@BookingActivity, "Pick a date or a time", Toast.LENGTH_SHORT)
                    .show()
            } else {
                bookingDialog(date!!, time!!, "tableSeven")
            }
        }

        binding.ivTableEight.setOnClickListener {

            if (date == null || time == null) {
                Toast.makeText(this@BookingActivity, "Pick a date or a time", Toast.LENGTH_SHORT)
                    .show()
            } else {
                bookingDialog(date!!, time!!, "tableEight")
            }
        }
    }

    private fun bookingDialog(date: String, time: String, tableNo: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm booking")
        builder.setMessage("Date: " + date + "\nTime: " + time + "\nTable Number: " + tableNo)
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            createBooking(date!!, time!!, tableNo)
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(
                applicationContext,
                android.R.string.no, Toast.LENGTH_SHORT
            ).show()
        }

        builder.show()
    }

    private fun createBooking(date: String, time: String, tableNo: String) =
        CoroutineScope(Dispatchers.IO).launch {

            val userDocRef = db.collection("users").document(auth?.currentUser?.uid.toString())
            val docSnapshot = userDocRef.get().await()
            Log.d("userDoc", docSnapshot.get("name").toString())

            val tableCollectionRef = db.collection("restaurants").document("flanagans").collection("tables").document(tableNo).collection(date)
            val querySnapshot = tableCollectionRef.get().await()

            val booking = hashMapOf(
                "time" to time,
                "name" to docSnapshot.get("name").toString(),
                "phoneNo" to docSnapshot.get("phoneNo").toString(),
                "email" to docSnapshot.get("emailAddress").toString(),
            )

            tableCollectionRef.document(time).set(booking).addOnSuccessListener {
                Log.d(TAG, "Booking successfully written!")
                Toast.makeText(
                    this@BookingActivity,
                    "Booking created!",
                    Toast.LENGTH_SHORT
                ).show()
                getAvailableTables(date, time)
                //personCollectionRef.document("Uykv5wvCCEcuIARFQ6hx").update("booking", "2pm 15th Jan")

                val booking = Booking(date, time, tableNo)
                personCollectionRef.document(auth?.uid.toString()).collection("booking").add(booking)
                    .addOnSuccessListener { Log.d(TAG, "User updated with booking!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating user with booking", e) }
            }.addOnFailureListener {
                    e -> Log.w(TAG, "Error writing document", e)
                Toast.makeText(
                    this@BookingActivity,
                    e.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }


            /**withContext(Dispatchers.Main) {
                when (querySnapshot.size()) {
                    0 -> Toast.makeText(
                        this@TableLayoutMessing,
                        "Date doesn't exist",
                        Toast.LENGTH_SHORT
                    ).show()
                    else -> Toast.makeText(
                        this@TableLayoutMessing,
                        "Date already exists",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } **/
            Log.d("checkIfDateExists", date + " " + tableNo + " " + querySnapshot.size().toString())
        }

    private fun getAvailableTables(date: String, time: String) =
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                setTablesAvailable()
            }
            val tables = mutableListOf(
                "tableOne",
                "tableTwo",
                "tableThree",
                "tableFour",
                "tableFive",
                "tableSix",
                "tableSeven",
                "tableEight"
            )
            for (i in tables) {
                val tableCollectionRef = db.collection("restaurants").document("flanagans").collection("tables").document(i).collection(date)

                try {
                    val querySnapshot = tableCollectionRef.get().await()
                    val sb = StringBuilder()
                    for (document in querySnapshot.documents) {

                        if (document.get("time") == time) {
                            //val table = i + " " + document.id + " time: " + document.get("time")
                            val table = document.get("time")
                            sb.append("$table\n")

                            withContext(Dispatchers.Main) {
                                when (i) {
                                    "tableOne" -> {
                                        binding.ivTableOne.setImageResource(R.drawable.ic_table_reserved_big)
                                        binding.ivTableOne.isClickable = false
                                    }
                                    "tableTwo" -> {
                                        binding.ivTableTwo.setImageResource(R.drawable.ic_table_reserved_big)
                                        binding.ivTableTwo.isClickable = false
                                    }
                                    "tableThree" -> {
                                        binding.ivTableThree.setImageResource(R.drawable.ic_table_reserved_big)
                                        binding.ivTableThree.isClickable = false
                                    }
                                    "tableFour" -> {
                                        binding.ivTableFour.setImageResource(R.drawable.ic_table_reserved_big)
                                        binding.ivTableFour.isClickable = false
                                    }
                                    "tableFive" -> {
                                        binding.ivTableFive.setImageResource(R.drawable.ic_table_reserved_big)
                                        binding.ivTableFive.isClickable = false
                                    }
                                    "tableSix" -> {
                                        binding.ivTableSix.setImageResource(R.drawable.ic_table_reserved_big)
                                        binding.ivTableSix.isClickable = false
                                    }
                                    "tableSeven" -> {
                                        binding.ivTableSeven.setImageResource(R.drawable.ic_table_reserved_big)
                                        binding.ivTableSeven.isClickable = false
                                    }
                                    "tableEight" -> {
                                        binding.ivTableEight.setImageResource(R.drawable.ic_table_reserved_big)
                                        binding.ivTableEight.isClickable = false
                                    }
                                }
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        //tvUsers.text = sb.toString()
                        Log.d("TestingTables", sb.toString())
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@BookingActivity, e.message, Toast.LENGTH_SHORT)
                            .show()
                        Log.d("getAvailableTables", e.message.toString())
                    }
                }
            }
        }

    private fun setTablesAvailable() {
        binding.ivTableOne.setImageResource(R.drawable.ic_table_available)
        binding.ivTableOne.isClickable = true

        binding.ivTableTwo.setImageResource(R.drawable.ic_table_available)
        binding.ivTableTwo.isClickable = true

        binding.ivTableThree.setImageResource(R.drawable.ic_table_available)
        binding.ivTableThree.isClickable = true

        binding.ivTableFour.setImageResource(R.drawable.ic_table_available)
        binding.ivTableFour.isClickable = true

        binding.ivTableFive.setImageResource(R.drawable.ic_table_available)
        binding.ivTableFive.isClickable = true

        binding.ivTableSix.setImageResource(R.drawable.ic_table_available)
        binding.ivTableSix.isClickable = true

        binding.ivTableSeven.setImageResource(R.drawable.ic_table_available)
        binding.ivTableSeven.isClickable = true

        binding.ivTableEight.setImageResource(R.drawable.ic_table_available)
        binding.ivTableEight.isClickable = true
    }

    private fun setDate() {
        val dpd =
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                var month = mMonth + 1
                date = "$mDay.$month.$mYear"
                binding.selectDateBtn.setText(date)
                //getAvailableTimes(date!!)

                binding.twoPmBtn.visibility = Button.VISIBLE
                binding.fourPmBtn.visibility = Button.VISIBLE
                binding.sixPmBtn.visibility = Button.VISIBLE
                binding.eightPmBtn.visibility = Button.VISIBLE

            }, year, month, day)
        dpd.datePicker.minDate = System.currentTimeMillis() - 1000
        dpd.show()
    }

    private fun getAvailableTimes(date: String, time: String) =
        CoroutineScope(Dispatchers.IO).launch {
            val tableCollectionRef = db.collection("Tables").document("tableEight").collection(date)

            try {
                val querySnapshot = tableCollectionRef.get().await()
                Log.d("TableLayoutMessing", querySnapshot.size().toString())

                withContext(Dispatchers.Main) {

                    binding.twoPmBtn.isClickable = true
                    binding.twoPmBtn.setBackgroundColor(Color.LTGRAY)
                    binding.twoPmBtn.setTextColor(Color.BLACK)

                    binding.fourPmBtn.isClickable = true
                    binding.fourPmBtn.setBackgroundColor(Color.LTGRAY)
                    binding.fourPmBtn.setTextColor(Color.BLACK)

                    binding.sixPmBtn.isClickable = true
                    binding.sixPmBtn.setBackgroundColor(Color.LTGRAY)
                    binding.sixPmBtn.setTextColor(Color.BLACK)

                    binding.eightPmBtn.isClickable = true
                    binding.eightPmBtn.setBackgroundColor(Color.LTGRAY)
                    binding.eightPmBtn.setTextColor(Color.BLACK)


                    for (document in querySnapshot.documents) {
                        Log.d("TableLayoutMessing", document.id)
                        if (document.id == "14.00") {
                            binding.twoPmBtn.isClickable = false
                            binding.twoPmBtn.setBackgroundColor(Color.DKGRAY)
                            binding.twoPmBtn.setTextColor(Color.WHITE)
                            //twoPmBtn.append(" (Reserved) ")
                        } else if (document.id == "16.00") {
                            binding.fourPmBtn.isClickable = false
                            binding.fourPmBtn.setBackgroundColor(Color.DKGRAY)
                            binding.fourPmBtn.setTextColor(Color.WHITE)
                            //fourPmBtn.append(" (Reserved) ")
                        } else if (document.id == "18.00") {
                            binding.sixPmBtn.isClickable = false
                            binding.sixPmBtn.setBackgroundColor(Color.DKGRAY)
                            binding.sixPmBtn.setTextColor(Color.WHITE)
                            //sixPmBtn.append(" (Reserved) ")
                        } else if (document.id == "20.00") {
                            binding.eightPmBtn.isClickable = false
                            binding.eightPmBtn.setBackgroundColor(Color.DKGRAY)
                            binding.eightPmBtn.setTextColor(Color.WHITE)
                            //eightPmBtn.append(" (Reserved) ")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@BookingActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
}