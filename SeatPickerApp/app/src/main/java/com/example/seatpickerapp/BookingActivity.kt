package com.example.seatpickerapp

import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        //binding.bookingScrollViewHoriz.setBackgroundResource(R.drawable.ic_floor_plan)

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
                binding.ivTableOne.setImageResource(R.drawable.ic_table_selected_big)
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
                binding.ivTableTwo.setImageResource(R.drawable.ic_table_selected_big)
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
                binding.ivTableThree.setImageResource(R.drawable.ic_table_selected_big)
                tableThreeSelected = true
            }
        }

        binding.ivTableFour.setOnClickListener {

            if (date == null || time == null || partySize == null) {
                Toast.makeText(this, "Pick a date, time and party size first", Toast.LENGTH_SHORT)
                    .show()
            } else {
                //bookingDialog(date!!, time!!, "tableFour")
                when (canSelectMultipleTables) {
                    false -> deSelectTables()
                }
                binding.ivTableFour.setImageResource(R.drawable.ic_table_selected_big)
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
                binding.ivTableFive.setImageResource(R.drawable.ic_table_selected_big)
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
                binding.ivTableSix.setImageResource(R.drawable.ic_table_selected_big)
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
                binding.ivTableSeven.setImageResource(R.drawable.ic_table_selected_big)
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
                binding.ivTableEight.setImageResource(R.drawable.ic_table_selected_big)
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

                    val splitBySpace =  sb.toString().split(" ")
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

    private fun multipleBookingDialog(date: String, time: String, tables: List<String>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm multiple bookings")
        builder.setMessage("Date: $date\nTime: $time\nTable Number: $tables")
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
                db.collection("restaurants").document("flanagans").collection("tables")
                    .document(tableNo).collection(date)
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
                                if (partySize.toInt() > querySnapshot["seats"].toString().toInt()) {
                                    binding.ivTableOne.setImageResource(R.drawable.ic_table_reserved_big)
                                    binding.ivTableOne.isClickable = false
                                }
                            }
                            "tableTwo" -> {
                                if (partySize.toInt() > querySnapshot["seats"].toString().toInt()) {
                                    binding.ivTableTwo.setImageResource(R.drawable.ic_table_reserved_big)
                                    binding.ivTableTwo.isClickable = false
                                }
                            }
                            "tableThree" -> {
                                if (partySize.toInt() > querySnapshot["seats"].toString().toInt()) {
                                    binding.ivTableThree.setImageResource(R.drawable.ic_table_reserved_big)
                                    binding.ivTableThree.isClickable = false
                                }
                            }
                            "tableFour" -> {
                                if (partySize.toInt() > querySnapshot["seats"].toString().toInt()) {
                                    binding.ivTableFour.setImageResource(R.drawable.ic_table_reserved_big)
                                    binding.ivTableFour.isClickable = false
                                }
                            }
                            "tableFive" -> {
                                if (partySize.toInt() > querySnapshot["seats"].toString().toInt()) {
                                    binding.ivTableFive.setImageResource(R.drawable.ic_table_reserved_big)
                                    binding.ivTableFive.isClickable = false
                                }
                            }
                            "tableSix" -> {
                                if (partySize.toInt() > querySnapshot["seats"].toString().toInt()) {
                                    binding.ivTableSix.setImageResource(R.drawable.ic_table_reserved_big)
                                    binding.ivTableSix.isClickable = false
                                }
                            }
                            "tableSeven" -> {
                                if (partySize.toInt() > querySnapshot["seats"].toString().toInt()) {
                                    binding.ivTableSeven.setImageResource(R.drawable.ic_table_reserved_big)
                                    binding.ivTableSeven.isClickable = false
                                }
                            }
                            "tableEight" -> {
                                if (partySize.toInt() > querySnapshot["seats"].toString().toInt()) {
                                    binding.ivTableEight.setImageResource(R.drawable.ic_table_reserved_big)
                                    binding.ivTableEight.isClickable = false
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

    private fun deSelectTables() {

            if (tableOneSelected) {
                tableOneSelected = false
                binding.ivTableOne.setImageResource(R.drawable.ic_table_available)
            }
            if (tableTwoSelected) {
                tableTwoSelected = false
                binding.ivTableTwo.setImageResource(R.drawable.ic_table_available)
            }
            if (tableThreeSelected) {
                tableThreeSelected = false
                binding.ivTableThree.setImageResource(R.drawable.ic_table_available)
            }
            if (tableFourSelected) {
                tableFourSelected = false
                binding.ivTableFour.setImageResource(R.drawable.ic_table_available)
            }
            if (tableFiveSelected) {
                tableFiveSelected = false
                binding.ivTableFive.setImageResource(R.drawable.ic_table_available)
            }
            if (tableSixSelected) {
                tableSixSelected = false
                binding.ivTableSix.setImageResource(R.drawable.ic_table_available)
            }
            if (tableSevenSelected) {
                tableSevenSelected = false
                binding.ivTableSeven.setImageResource(R.drawable.ic_table_available)
            }
            if (tableEightSelected) {
                tableEightSelected = false
                binding.ivTableEight.setImageResource(R.drawable.ic_table_available)
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

                binding.twoPmBtn.visibility = Button.VISIBLE
                binding.fourPmBtn.visibility = Button.VISIBLE
                binding.sixPmBtn.visibility = Button.VISIBLE
                binding.eightPmBtn.visibility = Button.VISIBLE

            }, year, month, day)
        dpd.datePicker.minDate = System.currentTimeMillis() - 1000
        dpd.show()
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
                Toast.makeText(this, "User picked yes", Toast.LENGTH_SHORT).show()
                canSelectMultipleTables = true
            }
            .setNegativeButton("Back") { dialogInterface, which ->
                setPartySize()
            }.create()

        multipleTableDialog.show()
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
