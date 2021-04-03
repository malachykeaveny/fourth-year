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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeSeatingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        binding.dateBtn.setOnClickListener {
            setDate()
        }

        binding.defaultBtn.setOnClickListener {
            setDefaultTablesLayout()
            tableLayout = "default"
        }

        binding.twoSeaterBtn.setOnClickListener {
            setTwoSeaterLayout()
            tableLayout = "twoSeater"
        }

        binding.fourSeaterBtn.setOnClickListener {
            setFourSeaterLayout()
            tableLayout = "fourSeater"
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

    private fun confirmLayoutDialog(date: String, tableLayout: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm table layout change")
        builder.setMessage("Date: $date \nLayout: $tableLayout")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            createTableLayoutChange(date, tableLayout)
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