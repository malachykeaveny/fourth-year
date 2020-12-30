package com.example.kotlinpractice

import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
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

    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    val db = FirebaseFirestore.getInstance()
    private var date: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_layout_messing)

        selectDateBtn.setOnClickListener{
            setDate()
        }

        ivTableOne.setOnClickListener{
            ivTableOne.setImageResource(R.drawable.ic_table_reserved_big)
        }

        twoPmBtn.setOnClickListener {
            getAvailableTimes(date!!, "14.00")
            getAvailableTables(date!!, "14.00")
        }
    }

    private fun getAvailableTables(date: String, time: String) = CoroutineScope(Dispatchers.IO).launch{
        val tables = mutableListOf("tableOne", "tableTwo", "tableThree", "tableFour", "tableFive", "tableSix", "tableSeven", "tableEight")
        for (i in tables) {
            val tableCollectionRef = db.collection("Tables").document(i).collection(date)

            try {
                val querySnapshot = tableCollectionRef.get().await()
                val sb = StringBuilder()
                for(document in querySnapshot.documents) {

                    if (document.get("time") == time) {
                        //val table = i + " " + document.id + " time: " + document.get("time")
                        val table = document.get("time")
                        sb.append("$table\n")

                        when (i) {
                            "tableOne" -> ivTableOne.setImageResource(R.drawable.ic_table_reserved_big)
                            "tableTwo" -> ivTableTwo.setImageResource(R.drawable.ic_table_reserved_big)
                            "tableFive" -> ivTableFive.setImageResource(R.drawable.ic_table_reserved_big)
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    //tvUsers.text = sb.toString()
                    Log.d("TestingTables", sb.toString())
                }

            }
            catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TableLayoutMessing, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setDate() {
        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{ view, mYear, mMonth, mDay ->
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

    private fun getAvailableTimes(date: String, time: String) = CoroutineScope(Dispatchers.IO).launch{
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


                for(document in querySnapshot.documents) {
                    Log.d("TableLayoutMessing", document.id)
                    if (document.id == "14.00") {
                        twoPmBtn.isClickable = false
                        twoPmBtn.setBackgroundColor(Color.DKGRAY)
                        twoPmBtn.setTextColor(Color.WHITE)
                        //twoPmBtn.append(" (Reserved) ")
                    }
                    else if (document.id == "16.00"){
                        fourPmBtn.isClickable = false
                        fourPmBtn.setBackgroundColor(Color.DKGRAY)
                        fourPmBtn.setTextColor(Color.WHITE)
                        //fourPmBtn.append(" (Reserved) ")
                    }
                    else if (document.id == "18.00"){
                        sixPmBtn.isClickable = false
                        sixPmBtn.setBackgroundColor(Color.DKGRAY)
                        sixPmBtn.setTextColor(Color.WHITE)
                        //sixPmBtn.append(" (Reserved) ")
                    }
                    else if (document.id == "20.00"){
                        eightPmBtn.isClickable = false
                        eightPmBtn.setBackgroundColor(Color.DKGRAY)
                        eightPmBtn.setTextColor(Color.WHITE)
                        //eightPmBtn.append(" (Reserved) ")
                    }
                }
            }
        }
        catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@TableLayoutMessing, e.message, Toast.LENGTH_SHORT).show()
            }
        }

    }
}