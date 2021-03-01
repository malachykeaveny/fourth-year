package com.example.seatpickerapp

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.seatpickerapp.databinding.ActivityBookingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
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
    private val db = FirebaseFirestore.getInstance()
    private var date: String? = null
    private var time: String? = null
    private var partySize: String? = null
    private val personCollectionRef = Firebase.firestore.collection("users")
    private var auth: FirebaseAuth? = null
    private var tableOneSelected: Boolean = false
    private var tableTwoSelected: Boolean = false
    private var tableThreeSelected: Boolean = false
    private var tableFourSelected: Boolean = false
    private var tableFiveSelected: Boolean = false
    private var tableSixSelected: Boolean = false
    private var tableSevenSelected: Boolean = false
    private var tableEightSelected: Boolean = false
    private var canSelectMultipleTables: Boolean = false
    private var specialLayout: String = ""
    private var tableOneSeats: Int = 2
    private var tableTwoSeats: Int = 2
    private var tableThreeSeats: Int = 2
    private var tableFourSeats: Int = 6
    private var tableFiveSeats: Int = 6
    private var tableSixSeats: Int = 4
    private var tableSevenSeats: Int = 4
    private var tableEightSeats: Int = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        //binding.bookingScrollViewHoriz.setBackgroundResource(R.drawable.wood_bg_light)
        //binding.bookingScrollView.setBackgroundResource(R.drawable.wood_bg_light)

        setPartySize()

        binding.partySizeBtn.setOnClickListener {
            setPartySize()
        }

        binding.selectDateBtn.setOnClickListener {
            setDate()
        }

        binding.twoPmBtn.setOnClickListener {
            deSelectTables()
            getReservedTables(date!!, "14.00")
            time = "14.00"
            when (canSelectMultipleTables) {
                false -> getUnsuitableTables(partySize!!)
            }
        }

        binding.fourPmBtn.setOnClickListener {
            deSelectTables()
            getReservedTables(date!!, "16.00")
            time = "16.00"
            when (canSelectMultipleTables) {
                false -> getUnsuitableTables(partySize!!)
            }
        }

        binding.sixPmBtn.setOnClickListener {
            deSelectTables()
            getReservedTables(date!!, "18.00")
            time = "18.00"
            when (canSelectMultipleTables) {
                false -> getUnsuitableTables(partySize!!)
            }
        }

        binding.eightPmBtn.setOnClickListener {
            deSelectTables()
            getReservedTables(date!!, "20.00")
            time = "20.00"
            when (canSelectMultipleTables) {
                false -> getUnsuitableTables(partySize!!)
            }
        }

        binding.ivTableOne.setOnClickListener {

            if (date == null || time == null || partySize == null) {
                Toast.makeText(this, "Pick a date, time and party size first", Toast.LENGTH_SHORT)
                    .show()
            } else {
                //bookingDialog(date!!, time!!, "tableOne")
                when (canSelectMultipleTables) {
                    false -> deSelectTables()
                }
                when (specialLayout) {
                    "default" -> binding.ivTableOne.setImageResource(R.drawable.ic_two_seater_dark_blue)
                    "twoSeater" -> binding.ivTableOne.setImageResource(R.drawable.ic_two_seater_dark_blue)
                    "fourSeater" -> binding.ivTableOne.setImageResource(R.drawable.ic_four_seater_dark_blue)
                }
                tableOneSelected = true
            }
        }

        binding.ivTableTwo.setOnClickListener {

            if (date == null || time == null || partySize == null) {
                Toast.makeText(this, "Pick a date, time and party size first", Toast.LENGTH_SHORT)
                    .show()
            } else {
                //bookingDialog(date!!, time!!, "tableTwo")
                when (canSelectMultipleTables) {
                    false -> deSelectTables()
                }
                when (specialLayout) {
                    "default" -> binding.ivTableTwo.setImageResource(R.drawable.ic_two_seater_dark_blue)
                    "twoSeater" -> binding.ivTableTwo.setImageResource(R.drawable.ic_two_seater_dark_blue)
                    "fourSeater" -> binding.ivTableTwo.setImageResource(R.drawable.ic_four_seater_dark_blue)
                }
                tableTwoSelected = true
            }
        }

        binding.ivTableThree.setOnClickListener {

            if (date == null || time == null || partySize == null) {
                Toast.makeText(this, "Pick a date, time and party size first", Toast.LENGTH_SHORT)
                    .show()
            } else {
                //bookingDialog(date!!, time!!, "tableThree")
                /**tableThreeSelected = when (tableThreeSelected) {
                false -> {
                binding.ivTableThree.setImageResource(R.drawable.ic_table_selected_big)
                true
                }
                true -> {
                binding.ivTableThree.setImageResource(R.drawable.ic_table_available)
                false
                }
                } */
                when (canSelectMultipleTables) {
                    false -> deSelectTables()
                }
                when (specialLayout) {
                    "default" -> binding.ivTableThree.setImageResource(R.drawable.ic_two_seater_dark_blue)
                    "twoSeater" -> binding.ivTableThree.setImageResource(R.drawable.ic_two_seater_dark_blue)
                    "fourSeater" -> binding.ivTableThree.setImageResource(R.drawable.ic_four_seater_dark_blue)
                }
                tableThreeSelected = true
            }
        }

        binding.ivTableFour.setOnClickListener {

            if (date == null || time == null || partySize == null) {
                Toast.makeText(this, "Pick a date, time and party size first", Toast.LENGTH_SHORT)
                    .show()
            } else {
                //Toast.makeText(this, "$specialLayout", Toast.LENGTH_SHORT).show()
                //Log.d("specialLayout", "$specialLayout")
                //bookingDialog(date!!, time!!, "tableFour")
                when (canSelectMultipleTables) {
                    false -> deSelectTables()
                }
                when (specialLayout) {
                    "default" -> binding.ivTableFour.setImageResource(R.drawable.ic_six_seater_dark_blue)
                    "twoSeater" -> binding.ivTableFour.setImageResource(R.drawable.ic_two_seater_dark_blue)
                    "fourSeater" -> binding.ivTableFour.setImageResource(R.drawable.ic_four_seater_dark_blue)
                }
                tableFourSelected = true
            }
        }

        binding.ivTableFive.setOnClickListener {

            if (date == null || time == null || partySize == null) {
                Toast.makeText(this, "Pick a date, time and party size first", Toast.LENGTH_SHORT)
                    .show()
            } else {
                //bookingDialog(date!!, time!!, "tableFive")
                when (canSelectMultipleTables) {
                    false -> deSelectTables()
                }
                when (specialLayout) {
                    "default" -> binding.ivTableFive.setImageResource(R.drawable.ic_six_seater_dark_blue)
                    "twoSeater" -> binding.ivTableFive.setImageResource(R.drawable.ic_two_seater_dark_blue)
                    "fourSeater" -> binding.ivTableFive.setImageResource(R.drawable.ic_four_seater_dark_blue)
                }
                tableFiveSelected = true
            }
        }

        binding.ivTableSix.setOnClickListener {

            if (date == null || time == null || partySize == null) {
                Toast.makeText(
                    this,
                    "Pick a date, time and party size first",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                //bookingDialog(date!!, time!!, "tableSix")
                when (canSelectMultipleTables) {
                    false -> deSelectTables()
                }
                when (specialLayout) {
                    "default" -> binding.ivTableSix.setImageResource(R.drawable.ic_four_seater_dark_blue)
                    "twoSeater" -> binding.ivTableSix.setImageResource(R.drawable.ic_two_seater_dark_blue)
                    "fourSeater" -> binding.ivTableSix.setImageResource(R.drawable.ic_four_seater_dark_blue)
                }
                tableSixSelected = true
            }
        }

        binding.ivTableSeven.setOnClickListener {

            if (date == null || time == null || partySize == null) {
                Toast.makeText(
                    this,
                    "Pick a date, time and party size first",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                //bookingDialog(date!!, time!!, "tableSeven")
                when (canSelectMultipleTables) {
                    false -> deSelectTables()
                }
                when (specialLayout) {
                    "default" -> binding.ivTableSeven.setImageResource(R.drawable.ic_four_seater_dark_blue)
                    "twoSeater" -> binding.ivTableSeven.setImageResource(R.drawable.ic_two_seater_dark_blue)
                    "fourSeater" -> binding.ivTableSeven.setImageResource(R.drawable.ic_four_seater_dark_blue)
                }
                tableSevenSelected = true
            }
        }

        binding.ivTableEight.setOnClickListener {
            if (date == null || time == null || partySize == null) {
                Toast.makeText(
                    this,
                    "Pick a date, time and party size first",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                //bookingDialog(date!!, time!!, "tableEight")
                when (canSelectMultipleTables) {
                    false -> deSelectTables()
                }
                when (specialLayout) {
                    "default" -> binding.ivTableEight.setImageResource(R.drawable.ic_four_seater_dark_blue)
                    "twoSeater" -> binding.ivTableEight.setImageResource(R.drawable.ic_two_seater_dark_blue)
                    "fourSeater" -> binding.ivTableEight.setImageResource(R.drawable.ic_four_seater_dark_blue)
                }
                tableEightSelected = true
            }
        }

        binding.bookBtn.setOnClickListener {
            when {
                date == null || time == null || partySize == null -> Toast.makeText(
                    this,
                    "Pick a date, time and party size first",
                    Toast.LENGTH_SHORT
                ).show()
                //!tableOneSelected && !tableTwoSelected && !tableThreeSelected && !tableFourSelected && !tableFiveSelected && !tableSixSelected && !tableSevenSelected && !tableEightSelected
                tableOneSelected == false && tableTwoSelected == false && tableThreeSelected == false && tableFourSelected == false && tableFiveSelected == false && tableSixSelected == false && tableSevenSelected == false && tableEightSelected == false
                -> Toast.makeText(this, "Please select at least one table", Toast.LENGTH_SHORT)
                    .show()
            }

            when (canSelectMultipleTables) {
                true -> {
                    val sb = StringBuilder()
                    sb.clear()
                    if (tableOneSelected)
                        sb.append("tableOne ")
                    if (tableTwoSelected)
                        sb.append("tableTwo ")
                    if (tableThreeSelected)
                        sb.append("tableThree ")
                    if (tableFourSelected)
                        sb.append("tableFour ")
                    if (tableFiveSelected)
                        sb.append("tableFive ")
                    if (tableSixSelected)
                        sb.append("tableSix ")
                    if (tableSevenSelected)
                        sb.append("tableSeven ")
                    if (tableEightSelected)
                        sb.append("tableEight ")

                    val splitBySpace = sb.toString().split(" ")
                    val listOfSelectedTables: MutableList<String> = splitBySpace.toMutableList()

                    if (listOfSelectedTables.count() > 1) {
                        listOfSelectedTables.removeLast()
                        multipleBookingDialog(date!!, time!!, listOfSelectedTables)
                    }


                    Log.d("mlSize", "size is " + listOfSelectedTables.count().toString())

                }
                false -> {
                    when {
                        tableOneSelected -> bookingDialog(date!!, time!!, "tableOne")
                        tableTwoSelected -> bookingDialog(date!!, time!!, "tableTwo")
                        tableThreeSelected -> bookingDialog(date!!, time!!, "tableThree")
                        tableFourSelected -> bookingDialog(date!!, time!!, "tableFour")
                        tableFiveSelected -> bookingDialog(date!!, time!!, "tableFive")
                        tableSixSelected -> bookingDialog(date!!, time!!, "tableSix")
                        tableSevenSelected -> bookingDialog(date!!, time!!, "tableSeven")
                        tableEightSelected -> bookingDialog(date!!, time!!, "tableEight")
                    }
                }
            }
        }

        binding.filamentBtn.setOnClickListener {
            startActivity(Intent(applicationContext, FilamentActivity::class.java))
        }

    }

    private fun bookingDialog(date: String, time: String, tableNo: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm booking")
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
        }
        builder.setMessage("Date: " + date + "\nTime: " + time + "\nTable Number: " + tableLong)
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

    private fun multipleBookingDialog(date: String, time: String, tables: List<String>) {

        var sb = StringBuilder()
        for (i in tables) {
            when (i) {
                "tableOne" -> sb.append("1, ")
                "tableTwo" -> sb.append("2, ")
                "tableThree" -> sb.append("3, ")
                "tableFour" -> sb.append("4, ")
                "tableFive" -> sb.append("5, ")
                "tableSix" -> sb.append("6, ")
                "tableSeven" -> sb.append("7, ")
                "tableEight" -> sb.append("8, ")
            }
        }


        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Booking")
        builder.setMessage("Date: $date\nTime: $time\nTable Numbers: ${sb.dropLast(2)}")
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            //createBooking(date!!, time!!, tableNo)
            for (i in tables) {
                createBooking(date, time, i)
            }
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

            val tableCollectionRef =
                db.collection("restaurants").document("flanagans").collection("tables").document(tableNo).collection(date)
            val querySnapshot = tableCollectionRef.get().await()

            val booking = hashMapOf(
                "time" to time,
                "name" to docSnapshot.get("name").toString(),
                "phoneNo" to docSnapshot.get("phoneNo").toString(),
                "email" to docSnapshot.get("emailAddress").toString(),
                "token" to docSnapshot.get("token").toString()
            )

            tableCollectionRef.document(time).set(booking).addOnSuccessListener {
                Log.d(TAG, "Booking successfully written!")
                Toast.makeText(
                    this@BookingActivity,
                    "Booking created!",
                    Toast.LENGTH_SHORT
                ).show()
                getReservedTables(date, time)
                //personCollectionRef.document("Uykv5wvCCEcuIARFQ6hx").update("booking", "2pm 15th Jan")

                val booking = Booking(date, time, tableNo)
                personCollectionRef.document(auth?.uid.toString()).collection("booking")
                    .add(booking)
                    .addOnSuccessListener { Log.d(TAG, "User updated with booking!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating user with booking", e) }
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
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

    private fun getUnsuitableTables(partySize: String) =
        CoroutineScope(Dispatchers.IO).launch {

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
                try {
                    val seatDocRef =
                        db.collection("restaurants").document("flanagans").collection("tables")
                            .document(i)
                    val querySnapshot = seatDocRef.get().await()

                    withContext(Dispatchers.Main) {

                        //Toast.makeText(this@BookingActivity, querySnapshot["seats"].toString() + " " + partySize.toInt(), Toast.LENGTH_SHORT).show()
                        //Log.d("getUnsuitableTables", querySnapshot["seats"] + " " + partySize.toInt())

                        when (i) {
                            //querySnapshot["seats"].toString().toInt() < partySize.toInt() -> Toast.makeText(this@BookingActivity, "gotem", Toast.LENGTH_SHORT).show()
                            "tableOne" -> {
                                if (partySize.toInt() > tableOneSeats) {
                                    binding.ivTableOne.animate().apply {
                                        duration = 750
                                        rotationXBy(180f)
                                    }.withEndAction {
                                        when (specialLayout) {
                                            "default" -> binding.ivTableOne.setImageResource(R.drawable.ic_two_seater_grey)
                                            "twoSeater" -> binding.ivTableOne.setImageResource(R.drawable.ic_two_seater_grey)
                                            "fourSeater" -> binding.ivTableOne.setImageResource(R.drawable.ic_four_seater_grey)
                                        }
                                        binding.ivTableOne.isClickable = false
                                    }.start()
                                }
                            }
                            "tableTwo" -> {
                                if (partySize.toInt() > tableTwoSeats) {
                                    binding.ivTableTwo.animate().apply {
                                        duration = 750
                                        rotationXBy(180f)
                                    }.withEndAction {
                                        when (specialLayout) {
                                            "default" -> binding.ivTableTwo.setImageResource(R.drawable.ic_two_seater_grey)
                                            "twoSeater" -> binding.ivTableTwo.setImageResource(R.drawable.ic_two_seater_grey)
                                            "fourSeater" -> binding.ivTableTwo.setImageResource(R.drawable.ic_four_seater_grey)
                                        }
                                        binding.ivTableTwo.isClickable = false
                                    }.start()
                                }
                            }
                            "tableThree" -> {
                                if (partySize.toInt() > tableThreeSeats) {
                                    binding.ivTableThree.animate().apply {
                                        duration = 750
                                        rotationXBy(180f)
                                    }.withEndAction {
                                        when (specialLayout) {
                                            "default" -> binding.ivTableThree.setImageResource(R.drawable.ic_two_seater_grey)
                                            "twoSeater" -> binding.ivTableThree.setImageResource(R.drawable.ic_two_seater_grey)
                                            "fourSeater" -> binding.ivTableThree.setImageResource(R.drawable.ic_four_seater_grey)
                                        }
                                        binding.ivTableThree.isClickable = false
                                    }.start()
                                }
                            }
                            "tableFour" -> {
                                if (partySize.toInt() > tableFourSeats) {
                                    binding.ivTableFour.animate().apply {
                                        duration = 750
                                        rotationXBy(180f)
                                    }.withEndAction {
                                        when (specialLayout) {
                                            "default" -> binding.ivTableFour.setImageResource(R.drawable.ic_six_seater_grey)
                                            "twoSeater" -> binding.ivTableFour.setImageResource(R.drawable.ic_two_seater_grey)
                                            "fourSeater" -> binding.ivTableFour.setImageResource(R.drawable.ic_four_seater_grey)
                                        }
                                        binding.ivTableFour.isClickable = false
                                    }.start()
                                }
                            }
                            "tableFive" -> {
                                if (partySize.toInt() > tableFiveSeats) {
                                    binding.ivTableFive.animate().apply {
                                        duration = 750
                                        rotationXBy(180f)
                                    }.withEndAction {
                                        when (specialLayout) {
                                            "default" -> binding.ivTableFive.setImageResource(R.drawable.ic_six_seater_grey)
                                            "twoSeater" -> binding.ivTableFive.setImageResource(R.drawable.ic_two_seater_grey)
                                            "fourSeater" -> binding.ivTableFive.setImageResource(R.drawable.ic_four_seater_grey)
                                        }
                                        binding.ivTableFive.isClickable = false
                                    }.start()
                                }
                            }
                            "tableSix" -> {
                                if (partySize.toInt() > tableSixSeats) {
                                    binding.ivTableSix.animate().apply {
                                        duration = 750
                                        rotationXBy(180f)
                                    }.withEndAction {
                                        when (specialLayout) {
                                            "default" -> binding.ivTableSix.setImageResource(R.drawable.ic_four_seater_grey)
                                            "twoSeater" -> binding.ivTableSix.setImageResource(R.drawable.ic_two_seater_grey)
                                            "fourSeater" -> binding.ivTableSix.setImageResource(R.drawable.ic_four_seater_grey)
                                        }
                                        binding.ivTableSix.isClickable = false
                                    }.start()
                                }
                            }
                            "tableSeven" -> {
                                if (partySize.toInt() > tableSevenSeats) {
                                    binding.ivTableSeven.animate().apply {
                                        duration = 750
                                        rotationXBy(180f)
                                    }.withEndAction {
                                        when (specialLayout) {
                                            "default" -> binding.ivTableSeven.setImageResource(R.drawable.ic_four_seater_grey)
                                            "twoSeater" -> binding.ivTableSeven.setImageResource(R.drawable.ic_two_seater_grey)
                                            "fourSeater" -> binding.ivTableSeven.setImageResource(R.drawable.ic_four_seater_grey)
                                        }
                                        binding.ivTableSeven.isClickable = false
                                    }.start()

                                }
                            }
                            "tableEight" -> {
                                if (partySize.toInt() > tableEightSeats) {
                                    binding.ivTableEight.animate().apply {
                                        duration = 750
                                        rotationXBy(180f)
                                    }.withEndAction {
                                        when (specialLayout) {
                                            "default" -> binding.ivTableEight.setImageResource(R.drawable.ic_four_seater_grey)
                                            "twoSeater" -> binding.ivTableEight.setImageResource(R.drawable.ic_two_seater_grey)
                                            "fourSeater" -> binding.ivTableEight.setImageResource(R.drawable.ic_four_seater_grey)
                                        }
                                        binding.ivTableEight.isClickable = false
                                    }.start()

                                }
                            }
                        }
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

    private fun getReservedTables(date: String, time: String) =
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
                val tableCollectionRef =
                    db.collection("restaurants").document("flanagans").collection("tables")
                        .document(i).collection(date)

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
                                        //binding.ivTableOne.setImageResource(R.drawable.ic_table_reserved_big)
                                        binding.ivTableOne.animate().apply {
                                            duration = 750
                                            rotationXBy(180f)
                                        }.withEndAction {
                                            when (specialLayout) {
                                                "default" -> binding.ivTableOne.setImageResource(R.drawable.ic_two_seater_black)
                                                "twoSeater" -> binding.ivTableOne.setImageResource(R.drawable.ic_two_seater_black)
                                                "fourSeater" -> binding.ivTableOne.setImageResource(R.drawable.ic_four_seater_black)
                                            }
                                            binding.ivTableOne.isClickable = false
                                        }.start()

                                    }
                                    "tableTwo" -> {

                                        binding.ivTableTwo.animate().apply {
                                            duration = 750
                                            rotationXBy(180f)
                                        }.withEndAction {
                                            when (specialLayout) {
                                                "default" -> binding.ivTableTwo.setImageResource(R.drawable.ic_two_seater_black)
                                                "twoSeater" -> binding.ivTableTwo.setImageResource(R.drawable.ic_two_seater_black)
                                                "fourSeater" -> binding.ivTableTwo.setImageResource(R.drawable.ic_four_seater_black)
                                            }
                                            binding.ivTableTwo.isClickable = false
                                        }.start()

                                    }
                                    "tableThree" -> {
                                        binding.ivTableThree.animate().apply {
                                            duration = 750
                                            rotationXBy(180f)
                                        }.withEndAction {
                                            when (specialLayout) {
                                                "default" -> binding.ivTableThree.setImageResource(R.drawable.ic_two_seater_black)
                                                "twoSeater" -> binding.ivTableThree.setImageResource(R.drawable.ic_two_seater_black)
                                                "fourSeater" -> binding.ivTableThree.setImageResource(R.drawable.ic_four_seater_black)
                                            }
                                            binding.ivTableThree.isClickable = false
                                        }.start()

                                    }
                                    "tableFour" -> {
                                        binding.ivTableFour.animate().apply {
                                            duration = 750
                                            rotationXBy(180f)
                                        }.withEndAction {
                                            when (specialLayout) {
                                                "default" -> binding.ivTableFour.setImageResource(R.drawable.ic_six_seater_black)
                                                "twoSeater" -> binding.ivTableFour.setImageResource(R.drawable.ic_two_seater_black)
                                                "fourSeater" -> binding.ivTableFour.setImageResource(R.drawable.ic_four_seater_black)
                                            }
                                            binding.ivTableFour.isClickable = false
                                        }.start()
                                    }
                                    "tableFive" -> {
                                        binding.ivTableFive.animate().apply {
                                            duration = 750
                                            rotationXBy(180f)
                                        }.withEndAction {
                                            when (specialLayout) {
                                                "default" -> binding.ivTableFive.setImageResource(R.drawable.ic_six_seater_black)
                                                "twoSeater" -> binding.ivTableFive.setImageResource(R.drawable.ic_two_seater_black)
                                                "fourSeater" -> binding.ivTableFive.setImageResource(R.drawable.ic_four_seater_black)
                                            }
                                            binding.ivTableFive.isClickable = false
                                        }.start()
                                    }
                                    "tableSix" -> {
                                        binding.ivTableSix.animate().apply {
                                            duration = 750
                                            rotationXBy(180f)
                                        }.withEndAction {
                                            when (specialLayout) {
                                                "default" -> binding.ivTableSix.setImageResource(R.drawable.ic_four_seater_black)
                                                "twoSeater" -> binding.ivTableSix.setImageResource(R.drawable.ic_two_seater_black)
                                                "fourSeater" -> binding.ivTableSix.setImageResource(R.drawable.ic_four_seater_black)
                                            }
                                            binding.ivTableSix.isClickable = false
                                        }.start()

                                    }
                                    "tableSeven" -> {
                                        binding.ivTableSeven.animate().apply {
                                            duration = 750
                                            rotationXBy(180f)
                                        }.withEndAction {
                                            when (specialLayout) {
                                                "default" -> binding.ivTableSeven.setImageResource(R.drawable.ic_four_seater_black)
                                                "twoSeater" -> binding.ivTableSeven.setImageResource(R.drawable.ic_two_seater_black)
                                                "fourSeater" -> binding.ivTableSeven.setImageResource(R.drawable.ic_four_seater_black)
                                            }
                                            binding.ivTableSeven.isClickable = false
                                        }.start()

                                    }
                                    "tableEight" -> {
                                        binding.ivTableEight.animate().apply {
                                            duration = 750
                                            rotationXBy(180f)
                                        }.withEndAction {
                                            when (specialLayout) {
                                                "default" -> binding.ivTableEight.setImageResource(R.drawable.ic_four_seater_black)
                                                "twoSeater" -> binding.ivTableEight.setImageResource(R.drawable.ic_two_seater_black)
                                                "fourSeater" -> binding.ivTableEight.setImageResource(R.drawable.ic_four_seater_black)
                                            }
                                            binding.ivTableEight.isClickable = false
                                        }.start()
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
        when (specialLayout) {
            "default" -> binding.ivTableOne.setImageResource(R.drawable.ic_two_seater_light_blue)
            "twoSeater" -> binding.ivTableOne.setImageResource(R.drawable.ic_two_seater_light_blue)
            "fourSeater" -> binding.ivTableOne.setImageResource(R.drawable.ic_four_seater_light_blue)
        }
        binding.ivTableOne.isClickable = true

        when (specialLayout) {
            "default" -> binding.ivTableTwo.setImageResource(R.drawable.ic_two_seater_light_blue)
            "twoSeater" -> binding.ivTableTwo.setImageResource(R.drawable.ic_two_seater_light_blue)
            "fourSeater" -> binding.ivTableTwo.setImageResource(R.drawable.ic_four_seater_light_blue)
        }
        binding.ivTableTwo.isClickable = true


        when (specialLayout) {
            "default" -> binding.ivTableThree.setImageResource(R.drawable.ic_two_seater_light_blue)
            "twoSeater" -> binding.ivTableThree.setImageResource(R.drawable.ic_two_seater_light_blue)
            "fourSeater" -> binding.ivTableThree.setImageResource(R.drawable.ic_four_seater_light_blue)
        }
        binding.ivTableThree.isClickable = true

        when (specialLayout) {
            "default" -> binding.ivTableFour.setImageResource(R.drawable.ic_six_seater_light_blue)
            "twoSeater" -> binding.ivTableFour.setImageResource(R.drawable.ic_two_seater_light_blue)
            "fourSeater" -> binding.ivTableFour.setImageResource(R.drawable.ic_four_seater_light_blue)
        }
        binding.ivTableFour.isClickable = true

        when (specialLayout) {
            "default" -> binding.ivTableFive.setImageResource(R.drawable.ic_six_seater_light_blue)
            "twoSeater" -> binding.ivTableFive.setImageResource(R.drawable.ic_two_seater_light_blue)
            "fourSeater" -> binding.ivTableFive.setImageResource(R.drawable.ic_four_seater_light_blue)
        }
        binding.ivTableFive.isClickable = true

        when (specialLayout) {
            "default" -> binding.ivTableSix.setImageResource(R.drawable.ic_four_seater_light_blue)
            "twoSeater" -> binding.ivTableSix.setImageResource(R.drawable.ic_two_seater_light_blue)
            "fourSeater" -> binding.ivTableSix.setImageResource(R.drawable.ic_four_seater_light_blue)
        }
        binding.ivTableSix.isClickable = true

        when (specialLayout) {
            "default" -> binding.ivTableSeven.setImageResource(R.drawable.ic_four_seater_light_blue)
            "twoSeater" -> binding.ivTableSeven.setImageResource(R.drawable.ic_two_seater_light_blue)
            "fourSeater" -> binding.ivTableSeven.setImageResource(R.drawable.ic_four_seater_light_blue)
        }
        binding.ivTableSeven.isClickable = true

        when (specialLayout) {
            "default" -> binding.ivTableEight.setImageResource(R.drawable.ic_four_seater_light_blue)
            "twoSeater" -> binding.ivTableEight.setImageResource(R.drawable.ic_two_seater_light_blue)
            "fourSeater" -> binding.ivTableEight.setImageResource(R.drawable.ic_four_seater_light_blue)
        }
        binding.ivTableEight.isClickable = true
    }

    private fun deSelectTables() {

        if (tableOneSelected) {
            tableOneSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableOne.setImageResource(R.drawable.ic_two_seater_light_blue)
                "twoSeater" -> binding.ivTableOne.setImageResource(R.drawable.ic_two_seater_light_blue)
                "fourSeater" -> binding.ivTableOne.setImageResource(R.drawable.ic_four_seater_light_blue)
            }

        }
        if (tableTwoSelected) {
            tableTwoSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableTwo.setImageResource(R.drawable.ic_two_seater_light_blue)
                "twoSeater" -> binding.ivTableTwo.setImageResource(R.drawable.ic_two_seater_light_blue)
                "fourSeater" -> binding.ivTableTwo.setImageResource(R.drawable.ic_four_seater_light_blue)
            }
        }
        if (tableThreeSelected) {
            tableThreeSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableThree.setImageResource(R.drawable.ic_two_seater_light_blue)
                "twoSeater" -> binding.ivTableThree.setImageResource(R.drawable.ic_two_seater_light_blue)
                "fourSeater" -> binding.ivTableThree.setImageResource(R.drawable.ic_four_seater_light_blue)
            }
        }
        if (tableFourSelected) {
            tableFourSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableFour.setImageResource(R.drawable.ic_six_seater_light_blue)
                "twoSeater" -> binding.ivTableFour.setImageResource(R.drawable.ic_two_seater_light_blue)
                "fourSeater" -> binding.ivTableFour.setImageResource(R.drawable.ic_four_seater_light_blue)
            }
        }
        if (tableFiveSelected) {
            tableFiveSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableFive.setImageResource(R.drawable.ic_six_seater_light_blue)
                "twoSeater" -> binding.ivTableFive.setImageResource(R.drawable.ic_two_seater_light_blue)
                "fourSeater" -> binding.ivTableFive.setImageResource(R.drawable.ic_four_seater_light_blue)
            }
        }
        if (tableSixSelected) {
            tableSixSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableSix.setImageResource(R.drawable.ic_four_seater_light_blue)
                "twoSeater" -> binding.ivTableSix.setImageResource(R.drawable.ic_two_seater_light_blue)
                "fourSeater" -> binding.ivTableSix.setImageResource(R.drawable.ic_four_seater_light_blue)
            }
        }
        if (tableSevenSelected) {
            tableSevenSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableSeven.setImageResource(R.drawable.ic_four_seater_light_blue)
                "twoSeater" -> binding.ivTableSeven.setImageResource(R.drawable.ic_two_seater_light_blue)
                "fourSeater" -> binding.ivTableSeven.setImageResource(R.drawable.ic_four_seater_light_blue)
            }
        }
        if (tableEightSelected) {
            tableEightSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableEight.setImageResource(R.drawable.ic_four_seater_light_blue)
                "twoSeater" -> binding.ivTableEight.setImageResource(R.drawable.ic_two_seater_light_blue)
                "fourSeater" -> binding.ivTableEight.setImageResource(R.drawable.ic_four_seater_light_blue)
            }
        }

        /**
        tableOneSelected = false
        binding.ivTableOne.setImageResource(R.drawable.ic_table_available)
        tableTwoSelected = false
        binding.ivTableTwo.setImageResource(R.drawable.ic_table_available)
        tableThreeSelected = false
        binding.ivTableThree.setImageResource(R.drawable.ic_table_available)
        tableFourSelected = false
        binding.ivTableFour.setImageResource(R.drawable.ic_table_available)
        tableFiveSelected = false
        binding.ivTableFive.setImageResource(R.drawable.ic_table_available)
        tableSixSelected = false
        binding.ivTableSix.setImageResource(R.drawable.ic_table_available)
        tableSevenSelected = false
        binding.ivTableSeven.setImageResource(R.drawable.ic_table_available)
        tableEightSelected = false
        binding.ivTableEight.setImageResource(R.drawable.ic_table_available)
         */

    }

    private fun setDate() {
        val dpd =
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                var month = mMonth + 1
                date = "$mDay.$month.$mYear"
                binding.selectDateBtn.setText(date)
                //getAvailableTimes(date!!)
                getSpecialTableLayout(date)

                binding.twoPmBtn.visibility = Button.VISIBLE
                binding.fourPmBtn.visibility = Button.VISIBLE
                binding.sixPmBtn.visibility = Button.VISIBLE
                binding.eightPmBtn.visibility = Button.VISIBLE

            }, year, month, day)
        dpd.datePicker.minDate = System.currentTimeMillis() - 1000
        dpd.show()
    }

    private fun getSpecialTableLayout(date: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val tableLayoutCollectionRef =
                db.collection("restaurants").document("flanagans").collection("tableLayouts")
            val querySnapshot = tableLayoutCollectionRef.get().await()
            specialLayout = ""
            for (document in querySnapshot.documents) {
                if (document.id == date) {
                    withContext(Dispatchers.Main) {
                        specialLayout = document.get("tableLayout").toString()
                        Log.d("BookingActLayout", "$specialLayout")
                        Toast.makeText(this@BookingActivity, "$specialLayout", Toast.LENGTH_SHORT).show()
                        checkTableLayout()
                    }
                }
            }
            if (specialLayout == "") {
                withContext(Dispatchers.Main) {
                    specialLayout = "default"
                    checkTableLayout()
                }
            }
        }
    }

    private fun checkTableLayout() {
        Log.d("specialLayout2", "$specialLayout")

        when (specialLayout) {
            "default" -> {
                setDefaultTablesLayout()
                tableOneSeats = 2
                tableTwoSeats = 2
                tableThreeSeats = 2
                tableFourSeats = 6
                tableFiveSeats = 6
                tableSixSeats = 4
                tableSevenSeats = 4
                tableEightSeats = 4
            }
            "twoSeater" -> {
                setTwoSeaterLayout()
                tableOneSeats = 2
                tableTwoSeats = 2
                tableThreeSeats = 2
                tableFourSeats = 2
                tableFiveSeats = 2
                tableSixSeats = 2
                tableSevenSeats = 2
                tableEightSeats = 2
            }
            "fourSeater" -> {
                setFourSeaterLayout()
                tableOneSeats = 4
                tableTwoSeats = 4
                tableThreeSeats = 4
                tableFourSeats = 4
                tableFiveSeats = 4
                tableSixSeats = 4
                tableSevenSeats = 4
                tableEightSeats = 4
            }
        }
    }

    private fun setDefaultTablesLayout() {
        val dpRatio = applicationContext.resources.displayMetrics.density
        val pixelForDP = 70 * dpRatio

        Log.d("dpTest", "${70 * pixelForDP}")
        val paramTableOne = binding.ivTableOne.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableTwo = binding.ivTableTwo.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableThree = binding.ivTableThree.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableFour = binding.ivTableFour.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableFive = binding.ivTableFive.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableSix = binding.ivTableSix.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableSeven = binding.ivTableSeven.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableEight = binding.ivTableEight.layoutParams as ViewGroup.MarginLayoutParams

        paramTableOne.topMargin = (20 * dpRatio).toInt()
        paramTableOne.leftMargin = (20 * dpRatio).toInt()
        binding.ivTableOne.layoutParams = paramTableOne
        binding.ivTableOne.scaleX = 1.25F
        binding.ivTableOne.scaleY = 1.25F

        paramTableTwo.topMargin = (170 * dpRatio).toInt()
        paramTableTwo.leftMargin = (20 * dpRatio).toInt()
        binding.ivTableTwo.layoutParams = paramTableTwo
        binding.ivTableTwo.scaleX = 1.25F
        binding.ivTableTwo.scaleY = 1.25F

        paramTableThree.topMargin = (320 * dpRatio).toInt()
        paramTableThree.leftMargin = (20 * dpRatio).toInt()
        binding.ivTableThree.layoutParams = paramTableThree
        binding.ivTableThree.scaleX = 1.25F
        binding.ivTableThree.scaleY = 1.25F

        paramTableFour.topMargin = (50 * dpRatio).toInt()
        paramTableFour.leftMargin = (250 * dpRatio).toInt()
        binding.ivTableFour.layoutParams = paramTableFour
        binding.ivTableFour.scaleX = 1.5F
        binding.ivTableFour.scaleY = 1.5F

        paramTableFive.topMargin = (200 * dpRatio).toInt()
        paramTableFive.leftMargin = (250 * dpRatio).toInt()
        binding.ivTableFive.layoutParams = paramTableFive
        binding.ivTableFive.scaleX = 1.5F
        binding.ivTableFive.scaleY = 1.5F

        paramTableSix.topMargin = (500 * dpRatio).toInt()
        paramTableSix.leftMargin = (20 * dpRatio).toInt()
        binding.ivTableSix.layoutParams = paramTableSix
        binding.ivTableSix.scaleX = 1.5F
        binding.ivTableSix.scaleY = 1.5F

        paramTableSeven.topMargin = (450 * dpRatio).toInt()
        paramTableSeven.leftMargin = (400 * dpRatio).toInt()
        binding.ivTableSeven.layoutParams = paramTableSeven
        binding.ivTableSeven.scaleX = 1.5F
        binding.ivTableSeven.scaleY = 1.5F

        paramTableEight.topMargin = (400 * dpRatio).toInt()
        paramTableEight.leftMargin = (250 * dpRatio).toInt()
        binding.ivTableEight.layoutParams = paramTableEight
        binding.ivTableEight.scaleX = 1.5F
        binding.ivTableEight.scaleY = 1.5F

        binding.ivTableOne.setImageResource(R.drawable.ic_two_seater_light_blue)
        binding.ivTableTwo.setImageResource(R.drawable.ic_two_seater_light_blue)
        binding.ivTableThree.setImageResource(R.drawable.ic_two_seater_light_blue)
        binding.ivTableFour.setImageResource(R.drawable.ic_six_seater_light_blue)
        binding.ivTableFive.setImageResource(R.drawable.ic_six_seater_light_blue)
        binding.ivTableSix.setImageResource(R.drawable.ic_four_seater_light_blue)
        binding.ivTableSeven.setImageResource(R.drawable.ic_four_seater_light_blue)
        binding.ivTableEight.setImageResource(R.drawable.ic_four_seater_light_blue)
    }

    private fun setTwoSeaterLayout() {
        val dpRatio = applicationContext.resources.displayMetrics.density
        val pixelForDP = 70 * dpRatio

        Log.d("dpTest", "${70 * pixelForDP}")
        val paramTableOne = binding.ivTableOne.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableTwo = binding.ivTableTwo.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableThree = binding.ivTableThree.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableFour = binding.ivTableFour.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableFive = binding.ivTableFive.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableSix = binding.ivTableSix.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableSeven = binding.ivTableSeven.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableEight = binding.ivTableEight.layoutParams as ViewGroup.MarginLayoutParams

        paramTableOne.topMargin = (50 * dpRatio).toInt()
        paramTableOne.leftMargin = (20 * dpRatio).toInt()
        binding.ivTableOne.layoutParams = paramTableOne
        binding.ivTableOne.scaleX = 1.25F
        binding.ivTableOne.scaleY = 1.25F

        paramTableTwo.topMargin = (250 * dpRatio).toInt()
        paramTableTwo.leftMargin = (20 * dpRatio).toInt()
        binding.ivTableTwo.layoutParams = paramTableTwo
        binding.ivTableTwo.scaleX = 1.25F
        binding.ivTableTwo.scaleY = 1.25F

        paramTableThree.topMargin = (450 * dpRatio).toInt()
        paramTableThree.leftMargin = (20 * dpRatio).toInt()
        binding.ivTableThree.layoutParams = paramTableThree
        binding.ivTableThree.scaleX = 1.25F
        binding.ivTableThree.scaleY = 1.25F

        paramTableFour.topMargin = (150 * dpRatio).toInt()
        paramTableFour.leftMargin = (175 * dpRatio).toInt()
        binding.ivTableFour.layoutParams = paramTableFour
        binding.ivTableFour.scaleX = 1.25F
        binding.ivTableFour.scaleY = 1.25F

        paramTableFive.topMargin = (350 * dpRatio).toInt()
        paramTableFive.leftMargin = (175 * dpRatio).toInt()
        binding.ivTableFive.layoutParams = paramTableFive
        binding.ivTableFive.scaleX = 1.25F
        binding.ivTableFive.scaleY = 1.25F

        paramTableSix.topMargin = (50 * dpRatio).toInt()
        paramTableSix.leftMargin = (330 * dpRatio).toInt()
        binding.ivTableSix.layoutParams = paramTableSix
        binding.ivTableSix.scaleX = 1.25F
        binding.ivTableSix.scaleY = 1.25F

        paramTableSeven.topMargin = (250 * dpRatio).toInt()
        paramTableSeven.leftMargin = (330 * dpRatio).toInt()
        binding.ivTableSeven.layoutParams = paramTableSeven
        binding.ivTableSeven.scaleX = 1.25F
        binding.ivTableSeven.scaleY = 1.25F

        paramTableEight.topMargin = (450 * dpRatio).toInt()
        paramTableEight.leftMargin = (330 * dpRatio).toInt()
        binding.ivTableEight.layoutParams = paramTableEight
        binding.ivTableEight.scaleX = 1.25F
        binding.ivTableEight.scaleY = 1.25F

        binding.ivTableOne.setImageResource(R.drawable.ic_two_seater_light_blue)
        binding.ivTableTwo.setImageResource(R.drawable.ic_two_seater_light_blue)
        binding.ivTableThree.setImageResource(R.drawable.ic_two_seater_light_blue)
        binding.ivTableFour.setImageResource(R.drawable.ic_two_seater_light_blue)
        binding.ivTableFive.setImageResource(R.drawable.ic_two_seater_light_blue)
        binding.ivTableSix.setImageResource(R.drawable.ic_two_seater_light_blue)
        binding.ivTableSeven.setImageResource(R.drawable.ic_two_seater_light_blue)
        binding.ivTableEight.setImageResource(R.drawable.ic_two_seater_light_blue)
    }

    private fun setFourSeaterLayout() {
        val dpRatio = applicationContext.resources.displayMetrics.density
        val pixelForDP = 70 * dpRatio

        Log.d("dpTest", "${70 * pixelForDP}")
        val paramTableOne = binding.ivTableOne.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableTwo = binding.ivTableTwo.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableThree = binding.ivTableThree.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableFour = binding.ivTableFour.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableFive = binding.ivTableFive.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableSix = binding.ivTableSix.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableSeven = binding.ivTableSeven.layoutParams as ViewGroup.MarginLayoutParams
        val paramTableEight = binding.ivTableEight.layoutParams as ViewGroup.MarginLayoutParams

        paramTableOne.topMargin = (50 * dpRatio).toInt()
        paramTableOne.leftMargin = (20 * dpRatio).toInt()
        binding.ivTableOne.layoutParams = paramTableOne
        binding.ivTableOne.scaleX = 1.5F
        binding.ivTableOne.scaleY = 1.5F

        paramTableTwo.topMargin = (250 * dpRatio).toInt()
        paramTableTwo.leftMargin = (20 * dpRatio).toInt()
        binding.ivTableTwo.layoutParams = paramTableTwo
        binding.ivTableTwo.scaleX = 1.5F
        binding.ivTableTwo.scaleY = 1.5F

        paramTableThree.topMargin = (450 * dpRatio).toInt()
        paramTableThree.leftMargin = (20 * dpRatio).toInt()
        binding.ivTableThree.layoutParams = paramTableThree
        binding.ivTableThree.scaleX = 1.5F
        binding.ivTableThree.scaleY = 1.5F

        paramTableFour.topMargin = (150 * dpRatio).toInt()
        paramTableFour.leftMargin = (175 * dpRatio).toInt()
        binding.ivTableFour.layoutParams = paramTableFour
        binding.ivTableFour.scaleX = 1.5F
        binding.ivTableFour.scaleY = 1.5F

        paramTableFive.topMargin = (350 * dpRatio).toInt()
        paramTableFive.leftMargin = (175 * dpRatio).toInt()
        binding.ivTableFive.layoutParams = paramTableFive
        binding.ivTableFive.scaleX = 1.5F
        binding.ivTableFive.scaleY = 1.5F

        paramTableSix.topMargin = (50 * dpRatio).toInt()
        paramTableSix.leftMargin = (330 * dpRatio).toInt()
        binding.ivTableSix.layoutParams = paramTableSix
        binding.ivTableSix.scaleX = 1.5F
        binding.ivTableSix.scaleY = 1.5F

        paramTableSeven.topMargin = (250 * dpRatio).toInt()
        paramTableSeven.leftMargin = (330 * dpRatio).toInt()
        binding.ivTableSeven.layoutParams = paramTableSeven
        binding.ivTableSeven.scaleX = 1.5F
        binding.ivTableSeven.scaleY = 1.5F

        paramTableEight.topMargin = (450 * dpRatio).toInt()
        paramTableEight.leftMargin = (330 * dpRatio).toInt()
        binding.ivTableEight.layoutParams = paramTableEight
        binding.ivTableEight.scaleX = 1.5F
        binding.ivTableEight.scaleY = 1.5F

        binding.ivTableOne.setImageResource(R.drawable.ic_four_seater_light_blue)
        binding.ivTableTwo.setImageResource(R.drawable.ic_four_seater_light_blue)
        binding.ivTableThree.setImageResource(R.drawable.ic_four_seater_light_blue)
        binding.ivTableFour.setImageResource(R.drawable.ic_four_seater_light_blue)
        binding.ivTableFive.setImageResource(R.drawable.ic_four_seater_light_blue)
        binding.ivTableSix.setImageResource(R.drawable.ic_four_seater_light_blue)
        binding.ivTableSeven.setImageResource(R.drawable.ic_four_seater_light_blue)
        binding.ivTableEight.setImageResource(R.drawable.ic_four_seater_light_blue)
    }

    private fun setPartySize() {
        val seatList = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        val partySizeDialog = AlertDialog.Builder(this)
            .setTitle("How many people are in you booking for?")
            .setSingleChoiceItems(seatList, -1) { dialogInterface, i -> }
            .setPositiveButton("Ok") { dialog, which ->
                val position = (dialog as AlertDialog).listView.checkedItemPosition

                if (position != -1) {
                    partySize = seatList[position]
                    //Toast.makeText(this, "You picked: $partySize", Toast.LENGTH_SHORT).show()
                    if (partySize!!.toInt() > 6) {
                        Toast.makeText(this, "Party size:  $partySize", Toast.LENGTH_SHORT).show()
                        askToBookMultipleTables(partySize!!)
                    } else {
                        canSelectMultipleTables = false
                        setDate()
                    }
                }
            }.create()

        partySizeDialog.show()

        partySizeDialog.setCancelable(false)
        partySizeDialog.setCanceledOnTouchOutside(false)
        partySizeDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        partySizeDialog.listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // enable positive button when user select an item
                partySizeDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .isEnabled = position != -1
            }
    }

    private fun askToBookMultipleTables(partySize: String) {
        val multipleTableDialog = AlertDialog.Builder(this)
            .setTitle("You have a table size of $partySize, do you want 2 tables?")
            .setPositiveButton("Yes") { dialog, which ->
                //Toast.makeText(this, "User picked yes", Toast.LENGTH_SHORT).show()
                canSelectMultipleTables = true
                setDate()
            }
            .setNegativeButton("Back") { dialogInterface, which ->
                setPartySize()
            }.create()

        multipleTableDialog.show()
        multipleTableDialog.setCancelable(false)
        multipleTableDialog.setCanceledOnTouchOutside(false)
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
