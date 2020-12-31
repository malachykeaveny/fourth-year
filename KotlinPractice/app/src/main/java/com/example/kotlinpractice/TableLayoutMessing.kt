package com.example.kotlinpractice

import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Contacts
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_button_attempt.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_reservation_step_two.*
import kotlinx.android.synthetic.main.activity_table_layout_messing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*

class TableLayoutMessing : AppCompatActivity() {

    private val TAG = "TableLayoutMessing"
    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    val db = FirebaseFirestore.getInstance()
    private var date: String? = null
    private var time: String? = null
    private val personCollectionRef = Firebase.firestore.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_layout_messing)
        view2.setBackgroundResource(R.drawable.ic_launcher_background)

        selectDateBtn.setOnClickListener {
            setDate()
        }

        ivTableOne.setOnClickListener {
            ivTableOne.setImageResource(R.drawable.ic_table_reserved_big)
        }

        twoPmBtn.setOnClickListener {
            //getAvailableTimes(date!!, "14.00")
            getAvailableTables(date!!, "14.00")
            time = "14.00"
        }

        fourPmBtn.setOnClickListener {
            getAvailableTables(date!!, "16.00")
            time = "16.00"
        }

        sixPmBtn.setOnClickListener {
            getAvailableTables(date!!, "18.00")
            time = "18.00"
        }

        eightPmBtn.setOnClickListener {
            getAvailableTables(date!!, "20.00")
            time = "20.00"
        }

        ivTableOne.setOnClickListener {

            if (date == null || time == null) {
                Toast.makeText(this@TableLayoutMessing, "Pick a date or a time", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Confirm booking")
                builder.setMessage("Date: " + date + "\nTime: " + time + "\nTable Number: " + "1")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    createBooking(date!!, time!!, "tableOne")
                }

                builder.setNegativeButton(android.R.string.no) { dialog, which ->
                    Toast.makeText(
                        applicationContext,
                        android.R.string.no, Toast.LENGTH_SHORT
                    ).show()
                }

                builder.show()
            }
        }
    }

    private fun createBooking(date: String, time: String, tableNo: String) =
        CoroutineScope(Dispatchers.IO).launch {
            val tableCollectionRef = db.collection("Tables").document(tableNo).collection(date)
            val querySnapshot = tableCollectionRef.get().await()

            val bookingTime = hashMapOf(
                "time" to time,
            )

            tableCollectionRef.document(time).set(bookingTime).addOnSuccessListener {
                Log.d(TAG, "Booking successfully written!")
                Toast.makeText(
                    this@TableLayoutMessing,
                    "Booking created!",
                    Toast.LENGTH_SHORT
                ).show()
                //personCollectionRef.document("Uykv5wvCCEcuIARFQ6hx").update("booking", "2pm 15th Jan")

                val booking = Booking(date, time, tableNo)
                personCollectionRef.document("Uykv5wvCCEcuIARFQ6hx").collection("booking").add(booking)
                    .addOnSuccessListener { Log.d(TAG, "User updated with booking!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating user with booking", e) }
            }.addOnFailureListener {
                    e -> Log.w(TAG, "Error writing document", e)
                Toast.makeText(
                    this@TableLayoutMessing,
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
                val tableCollectionRef = db.collection("Tables").document(i).collection(date)

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
                                    "tableOne" -> ivTableOne.setImageResource(R.drawable.ic_table_reserved_big)
                                    "tableTwo" -> ivTableTwo.setImageResource(R.drawable.ic_table_reserved_big)
                                    "tableThree" -> ivTableThree.setImageResource(R.drawable.ic_table_reserved_big)
                                    "tableFour" -> ivTableFour.setImageResource(R.drawable.ic_table_reserved_big)
                                    "tableFive" -> {
                                        ivTableFive.setImageResource(R.drawable.ic_table_reserved_big)
                                        ivTableFive.isClickable = false
                                    }
                                    "tableSix" -> ivTableSix.setImageResource(R.drawable.ic_table_reserved_big)
                                    "tableSeven" -> ivTableSeven.setImageResource(R.drawable.ic_table_reserved_big)
                                    "tableEight" -> ivTableEight.setImageResource(R.drawable.ic_table_reserved_big)
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
                        Toast.makeText(this@TableLayoutMessing, e.message, Toast.LENGTH_SHORT)
                            .show()
                        Log.d("getAvailableTables", e.message.toString())
                    }
                }
            }
        }

    private fun setTablesAvailable() {
        ivTableOne.setImageResource(R.drawable.ic_table_available)
        ivTableOne.isClickable = true

        ivTableTwo.setImageResource(R.drawable.ic_table_available)
        ivTableTwo.isClickable = true

        ivTableThree.setImageResource(R.drawable.ic_table_available)
        ivTableThree.isClickable = true

        ivTableFour.setImageResource(R.drawable.ic_table_available)
        ivTableFour.isClickable = true

        ivTableFive.setImageResource(R.drawable.ic_table_available)
        ivTableFive.isClickable = true

        ivTableSix.setImageResource(R.drawable.ic_table_available)
        ivTableSix.isClickable = true

        ivTableSeven.setImageResource(R.drawable.ic_table_available)
        ivTableSeven.isClickable = true

        ivTableEight.setImageResource(R.drawable.ic_table_available)
        ivTableEight.isClickable = true
    }

    private fun setDate() {
        val dpd =
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                var month = mMonth + 1
                date = "$mDay.$month.$mYear"
                selectDateBtn.setText(date)
                //getAvailableTimes(date!!)

                twoPmBtn.visibility = Button.VISIBLE
                fourPmBtn.visibility = Button.VISIBLE
                sixPmBtn.visibility = Button.VISIBLE
                eightPmBtn.visibility = Button.VISIBLE

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

                    twoPmBtn.isClickable = true
                    twoPmBtn.setBackgroundColor(Color.LTGRAY)
                    twoPmBtn.setTextColor(Color.BLACK)

                    fourPmBtn.isClickable = true
                    fourPmBtn.setBackgroundColor(Color.LTGRAY)
                    fourPmBtn.setTextColor(Color.BLACK)

                    sixPmBtn.isClickable = true
                    sixPmBtn.setBackgroundColor(Color.LTGRAY)
                    sixPmBtn.setTextColor(Color.BLACK)

                    eightPmBtn.isClickable = true
                    eightPmBtn.setBackgroundColor(Color.LTGRAY)
                    eightPmBtn.setTextColor(Color.BLACK)


                    for (document in querySnapshot.documents) {
                        Log.d("TableLayoutMessing", document.id)
                        if (document.id == "14.00") {
                            twoPmBtn.isClickable = false
                            twoPmBtn.setBackgroundColor(Color.DKGRAY)
                            twoPmBtn.setTextColor(Color.WHITE)
                            //twoPmBtn.append(" (Reserved) ")
                        } else if (document.id == "16.00") {
                            fourPmBtn.isClickable = false
                            fourPmBtn.setBackgroundColor(Color.DKGRAY)
                            fourPmBtn.setTextColor(Color.WHITE)
                            //fourPmBtn.append(" (Reserved) ")
                        } else if (document.id == "18.00") {
                            sixPmBtn.isClickable = false
                            sixPmBtn.setBackgroundColor(Color.DKGRAY)
                            sixPmBtn.setTextColor(Color.WHITE)
                            //sixPmBtn.append(" (Reserved) ")
                        } else if (document.id == "20.00") {
                            eightPmBtn.isClickable = false
                            eightPmBtn.setBackgroundColor(Color.DKGRAY)
                            eightPmBtn.setTextColor(Color.WHITE)
                            //eightPmBtn.append(" (Reserved) ")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TableLayoutMessing, e.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
}