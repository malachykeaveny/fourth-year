package com.example.seatpickerapp.activities

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.seatpickerapp.R
import com.example.seatpickerapp.databinding.ActivityChangeSeatingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class ChangeSeatingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeSeatingBinding
    private var date: String? = null
    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    private var tableLayout: String ?= null
    private var auth: FirebaseAuth? = null
    private val personCollectionRef = Firebase.firestore.collection("users")
    private val db = FirebaseFirestore.getInstance()
    private var adminCurrentRestaurant: String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeSeatingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        adminCurrentRestaurant = AdminHomeActivity.adminRestaurantName.replace("\\s".toRegex(), "").decapitalize(Locale.ROOT)
        Log.d("ChangeSeatingActivity", adminCurrentRestaurant!!)

        binding.dateBtn.setOnClickListener {
            setDate()
        }

        binding.twoMetreCardView.setOnClickListener {
            tableLayout = "default"
        }

        binding.fourMetreCardView.setOnClickListener {
            tableLayout = "fourMetre"
        }



        binding.confirmSeatChangeBtn.setOnClickListener {
            if (date == null || tableLayout == null) {
                Toast.makeText(this, "Pick a date and table layout first!", Toast.LENGTH_SHORT).show()
            }
            else if (tableLayout == "default") {
                Toast.makeText(this, "Cannot change to default layout!", Toast.LENGTH_SHORT).show()
            }
            else {
                confirmLayoutDialog(date!!, tableLayout!!)
            }
        }

    }

    private fun setFourMetreLayoutFlanagans() {

        db.collection("restaurants").document(adminCurrentRestaurant!!).collection("bookingsMgmt").document("tableBookings").collection(date!!)
            .get()
            .addOnSuccessListener { result ->

                for (document in result) {
                    Log.d("ChangeSeating", "${document.id} => ${document.data}")
                }

                if (result.size() > 5) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Warning")
                    builder.setMessage("Cannot change layout as there are many bookings already on this date")

                    builder.setPositiveButton("Cancel") { dialog, which ->

                    }

                    builder.show()
                }
                else {
                    val tableLayoutDocRef = db.collection("restaurants").document("flanagans").collection("tableLayouts").document(date!!)
                    tableLayoutDocRef.get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                Toast.makeText(this, "A special layout already exists for $date !", Toast.LENGTH_SHORT).show()
                                Log.d("ChangeSeatingActivity", "DocumentSnapshot data: ${document.data}")
                            } else {
                                Log.d("ChangeSeatingActivity", "No such document")

                                val layoutHashMap = hashMapOf(
                                    "date" to date,
                                    "tableLayout" to tableLayout,
                                    "tableOneVisible" to true,
                                    "tableTwoVisible" to false,
                                    "tableThreeVisible" to true,
                                    "tableFourVisible" to true,
                                    "tableFiveVisible" to true,
                                    "tableSixVisible" to false,
                                    "tableSevenVisible" to false,
                                    "tableEightVisible" to false,
                                )

                                tableLayoutDocRef.set(layoutHashMap).addOnSuccessListener {
                                    Log.d("seatingChangeConfirm", "Layout change successfully written!")
                                    Toast.makeText(this@ChangeSeatingActivity, "Layout change successfully written!", Toast.LENGTH_SHORT).show()
                                }.addOnFailureListener { e ->
                                    Log.w("seatingChangeConfirmF", "Error writing document", e)
                                    Toast.makeText(this@ChangeSeatingActivity, e.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("ChangeSeatingActivity", "get failed with ", exception)
                        }
                }

            }
            .addOnFailureListener { exception ->
                Log.d("ChangeSeating", "Error getting documents: ", exception)
            }
    }

    private fun setFourMetreLayoutOakFirePizza() {

        db.collection("restaurants").document(adminCurrentRestaurant!!).collection("bookingsMgmt").document("tableBookings").collection(date!!)
            .get()
            .addOnSuccessListener { result ->

                for (document in result) {
                    Log.d("ChangeSeating", "${document.id} => ${document.data}")
                }

                if (result.size() > 5) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Warning")
                    builder.setMessage("Cannot change layout as there are many bookings already on this date")

                    builder.setPositiveButton("Dismiss") { dialog, which ->

                    }

                    builder.show()
                }
                else {
                    val tableLayoutDocRef = db.collection("restaurants").document("oakFirePizza").collection("tableLayouts").document(date!!)
                    tableLayoutDocRef.get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                Toast.makeText(this, "A special layout already exists for $date !", Toast.LENGTH_SHORT).show()
                                Log.d("ChangeSeatingActivity", "DocumentSnapshot data: ${document.data}")
                            } else {
                                Log.d("ChangeSeatingActivity", "No such document")

                                val layoutHashMap = hashMapOf(
                                    "date" to date,
                                    "tableLayout" to tableLayout,
                                    "tableOneVisible" to true,
                                    "tableTwoVisible" to false,
                                    "tableThreeVisible" to true,
                                    "tableFourVisible" to true,
                                    "tableFiveVisible" to true,
                                    "tableSixVisible" to false,
                                    "tableSevenVisible" to true,
                                    "tableEightVisible" to false,
                                    "tableNineVisible" to true,
                                    "tableTenVisible" to false
                                )

                                tableLayoutDocRef.set(layoutHashMap).addOnSuccessListener {
                                    Log.d("seatingChangeConfirm", "Layout change successfully written!")
                                    Toast.makeText(this@ChangeSeatingActivity, "Layout change successfully written!", Toast.LENGTH_SHORT).show()
                                }.addOnFailureListener { e ->
                                    Log.w("seatingChangeConfirmF", "Error writing document", e)
                                    Toast.makeText(this@ChangeSeatingActivity, e.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("ChangeSeatingActivity", "get failed with ", exception)
                        }
                }

            }
            .addOnFailureListener { exception ->
                Log.d("ChangeSeating", "Error getting documents: ", exception)
            }

    }

    private fun confirmLayoutDialog(date: String, tableLayout: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm table layout change")
        builder.setMessage("Restaurant: ${adminCurrentRestaurant}\nDate: $date \nLayout: $tableLayout")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            //createTableLayoutChange(date, tableLayout)
            when (adminCurrentRestaurant) {
                "flanagans" -> setFourMetreLayoutFlanagans()
                "oakFirePizza" -> setFourMetreLayoutOakFirePizza()
            }
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(applicationContext, android.R.string.no, Toast.LENGTH_SHORT).show()
        }

        builder.show()
    }



    private fun createTableLayoutChange(date: String, tableLayout: String) =
        CoroutineScope(Dispatchers.IO).launch {
            val tableLayoutCollectionRef = db.collection("restaurants").document("flanagans").collection("tableLayouts")
            val querySnapshot = tableLayoutCollectionRef.get().await()

            val docRef = db.collection("restaurants").document("flanagans").collection("tableLayouts").document(date)
            val docSnapshot = docRef.get().await()

            if (docSnapshot.exists()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ChangeSeatingActivity,
                        "Special layout already exists for $date !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else {
                val layoutHashMap = hashMapOf(
                    "date" to date,
                    "tableLayout" to tableLayout
                )

                tableLayoutCollectionRef.document(date).set(layoutHashMap).addOnSuccessListener {
                    Log.d("seatingChangeConfirm", "Layout change successfully written!")
                    Toast.makeText(this@ChangeSeatingActivity, "Layout change successfully written!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    Log.w("seatingChangeConfirmF", "Error writing document", e)
                    Toast.makeText(
                        this@ChangeSeatingActivity,
                        e.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }





    private fun setDate() {
        val dpd =
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                var month = mMonth + 1
                date = "$mDay.$month.$mYear"
                binding.dateBtn.setText(date)
                //getAvailableTimes(date!!)

            }, year, month, day)
        dpd.datePicker.minDate = System.currentTimeMillis() - 1000
        dpd.show()
    }



}