 package com.example.seatpickerapp.activities

import android.R
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.seatpickerapp.databinding.ActivityStaffToTablesBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.StringBuilder
import java.util.*


class StaffToTablesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaffToTablesBinding
    private var date: String? = null
    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    private val db = FirebaseFirestore.getInstance()
    val subjects: MutableList<String?> = ArrayList()
    private var spinner: Spinner? = null
    private var tableOneSelected: Boolean = false
    private var tableTwoSelected: Boolean = false
    private var tableThreeSelected: Boolean = false
    private var tableFourSelected: Boolean = false
    private var tableFiveSelected: Boolean = false
    private var tableSixSelected: Boolean = false
    private var tableSevenSelected: Boolean = false
    private var tableEightSelected: Boolean = false
    private var selectedStaffMember: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffToTablesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        spinner = binding.spinner

        binding.dateBtn.setOnClickListener {
            setDate()
        }

        spinner()

        spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //Toast.makeText(applicationContext, "${subjects[position]}", Toast.LENGTH_SHORT).show()
                Log.d("spinner", "${subjects[position]}")
                selectedStaffMember = subjects[position]
                deSelectTables()
            }

        }

        binding.ivTableOne.setOnClickListener {
            if (date == null || selectedStaffMember == null) {
                Toast.makeText(this, "Pick a date and employee first!", Toast.LENGTH_SHORT).show()
            } else {
                binding.ivTableOne.setImageResource(com.example.seatpickerapp.R.drawable.ic_two_seater_dark_blue)
                tableOneSelected = true
            }
        }

        binding.ivTableTwo.setOnClickListener {
            if (date == null || selectedStaffMember == null) {
                Toast.makeText(this, "Pick a date, time and party size first", Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.ivTableTwo.setImageResource(com.example.seatpickerapp.R.drawable.ic_two_seater_dark_blue)
                tableTwoSelected = true
            }
        }

        binding.ivTableThree.setOnClickListener {
            if (date == null || selectedStaffMember == null) {
                Toast.makeText(this, "Pick a date, time and party size first", Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.ivTableThree.setImageResource(com.example.seatpickerapp.R.drawable.ic_two_seater_dark_blue)
                tableThreeSelected = true
            }
        }

        binding.ivTableFour.setOnClickListener {
            if (date == null || selectedStaffMember == null) {
                Toast.makeText(this, "Pick a date, time and party size first", Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.ivTableFour.setImageResource(com.example.seatpickerapp.R.drawable.ic_six_seater_dark_blue)
                tableFourSelected = true
            }
        }

        binding.ivTableFive.setOnClickListener {
            if (date == null || selectedStaffMember == null) {
                Toast.makeText(this, "Pick a date, time and party size first", Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.ivTableFive.setImageResource(com.example.seatpickerapp.R.drawable.ic_six_seater_dark_blue)
                tableFiveSelected = true
            }
        }

        binding.ivTableSix.setOnClickListener {
            if (date == null || selectedStaffMember == null) {
                Toast.makeText(this, "Pick a date, time and party size first", Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.ivTableSix.setImageResource(com.example.seatpickerapp.R.drawable.ic_four_seater_dark_blue)
                tableSixSelected = true
            }
        }

        binding.ivTableSeven.setOnClickListener {
            if (date == null || selectedStaffMember == null) {
                Toast.makeText(this, "Pick a date, time and party size first", Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.ivTableSeven.setImageResource(com.example.seatpickerapp.R.drawable.ic_four_seater_dark_blue)
                tableSevenSelected = true
            }
        }

        binding.ivTableEight.setOnClickListener {
            if (date == null || selectedStaffMember == null) {
                Toast.makeText(this, "Pick a date, time and party size first", Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.ivTableEight.setImageResource(com.example.seatpickerapp.R.drawable.ic_four_seater_dark_blue)
                tableEightSelected = true
            }
        }

        binding.confirmStaffToTable.setOnClickListener {
            if (date == null || selectedStaffMember == null) {
                Toast.makeText(this, "Pick a date, time and party size first", Toast.LENGTH_SHORT)
                    .show()
            } else {
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
                    confirmStaffToTablesDialog(date!!, listOfSelectedTables)
                }
            }
        }

    }

    private fun spinner() {
        val staffCollectionRef = db.collection("restaurants").document("flanagans").collection("staff")
        val adapter = ArrayAdapter(applicationContext, R.layout.simple_spinner_item, subjects)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = adapter
        staffCollectionRef.get().addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    val subject = document.getString("name")
                    subjects.add(subject)
                }
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun setDate() {
        val dpd =
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                var month = mMonth + 1
                date = "$mDay.$month.$mYear"
                binding.dateBtn.setText(date)
                //getAvailableTimes(date!!)


            }, year, month, day)
        //dpd.datePicker.minDate = System.currentTimeMillis() - 1000
        dpd.show()
    }

    private fun confirmStaffToTablesDialog(date: String, tables: List<String>) {
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
        builder.setTitle("Assign tables to employee")
        builder.setMessage(
            "Employee: $selectedStaffMember \nDate: $date \nTable Numbers: ${sb.dropLast(2)}"
        )
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            //createBooking(date!!, time!!, tableNo)
            for (i in tables) {
                createStaffToTable(date, selectedStaffMember!!, i)
                deSelectTables()
            }
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(
                applicationContext,
                android.R.string.no, Toast.LENGTH_SHORT
            ).show()
            deSelectTables()
        }

        builder.show()
    }

    private fun createStaffToTable(date: String, selectedStaffMember: String, tableNo: String) =
        CoroutineScope(Dispatchers.IO).launch {
            val tableLayoutCollectionRef =
                db.collection("restaurants").document("flanagans").collection("staff")
                    .document(selectedStaffMember).collection(date)
            val querySnapshot = tableLayoutCollectionRef.get().await()

            val docRef = db.collection("restaurants").document("flanagans").collection("staff")
                .document(selectedStaffMember).collection(date).document(tableNo)
            val docSnapshot = docRef.get().await()

            if (docSnapshot.exists()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@StaffToTablesActivity,
                        "You've already assigned $selectedStaffMember 's tables for $date !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val layoutHashMap = hashMapOf(
                    "tableNo" to tableNo,
                )

                tableLayoutCollectionRef.document(tableNo).set(layoutHashMap).addOnSuccessListener {
                    Log.d("seatingChangeConfirm", "Layout change successfully written!")
                    Toast.makeText(
                        this@StaffToTablesActivity,
                        "Staff to table successfully written!",
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener { e ->
                    Log.w("seatingChangeConfirmF", "Error writing document", e)
                    Toast.makeText(
                        this@StaffToTablesActivity,
                        e.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


    private fun deSelectTables() {

        if (tableOneSelected) {
            tableOneSelected = false
            binding.ivTableOne.setImageResource(com.example.seatpickerapp.R.drawable.ic_two_seater_light_blue)
        }
        if (tableTwoSelected) {
            tableTwoSelected = false
            binding.ivTableTwo.setImageResource(com.example.seatpickerapp.R.drawable.ic_two_seater_light_blue)
        }
        if (tableThreeSelected) {
            tableThreeSelected = false
            binding.ivTableThree.setImageResource(com.example.seatpickerapp.R.drawable.ic_two_seater_light_blue)
        }
        if (tableFourSelected) {
            tableFourSelected = false
            binding.ivTableFour.setImageResource(com.example.seatpickerapp.R.drawable.ic_six_seater_light_blue)
        }
        if (tableFiveSelected) {
            tableFiveSelected = false
            binding.ivTableFive.setImageResource(com.example.seatpickerapp.R.drawable.ic_six_seater_light_blue)
        }
        if (tableSixSelected) {
            tableSixSelected = false
            binding.ivTableSix.setImageResource(com.example.seatpickerapp.R.drawable.ic_four_seater_light_blue)
        }
        if (tableSevenSelected) {
            tableSevenSelected = false
            binding.ivTableSeven.setImageResource(com.example.seatpickerapp.R.drawable.ic_four_seater_light_blue)
        }
        if (tableEightSelected) {
            tableEightSelected = false
            binding.ivTableEight.setImageResource(com.example.seatpickerapp.R.drawable.ic_four_seater_light_blue)
        }
    }

}