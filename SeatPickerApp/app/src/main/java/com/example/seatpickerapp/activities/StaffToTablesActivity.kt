 package com.example.seatpickerapp.activities

import android.R
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.seatpickerapp.dataClasses.CartItem
import com.example.seatpickerapp.dataClasses.Tables
import com.example.seatpickerapp.databinding.ActivityStaffToTablesBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
    private var selectedStaffMember: String? = null
    private var adapter: StaffToTablesActivity.ProductFirestoreRecyclerAdapter?= null
    private var restaurant: String? = null
    private var tt: String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffToTablesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        restaurant = intent.getStringExtra("restaurant").toString().decapitalize(Locale.ROOT)
            .replace("\\s".toRegex(), "")
        Log.d("StaffToTablesActivity", restaurant!!)

        spinner = binding.spinner

        val tableCollectionRef = db.collection("restaurants").document(restaurant!!).collection("tables")
        binding.staffToTablesRv.layoutManager = GridLayoutManager(applicationContext, 2)
        //binding.staffToTablesRv.layoutManager = LinearLayoutManager(applicationContext)
        val options = FirestoreRecyclerOptions.Builder<Tables>().setQuery(tableCollectionRef, Tables::class.java).build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.staffToTablesRv.adapter = adapter

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
            }
        }


    }

    private fun spinner() {
        val staffCollectionRef = db.collection("restaurants").document(restaurant!!).collection("staff")
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

    private fun validateData(tableNo: String) {
        if (date == null || selectedStaffMember == null) {
            Toast.makeText(this@StaffToTablesActivity, "Please select a date and staff member first!", Toast.LENGTH_SHORT).show()
        }
        else {
            createStaffToTable(date!!, selectedStaffMember!!, tableNo)
        }
    }

    private fun createStaffToTable(date: String, selectedStaffMember: String, tableNo: String) =
        CoroutineScope(Dispatchers.IO).launch {
            val tableLayoutCollectionRef =
                db.collection("restaurants").document(restaurant!!).collection("staff")
                    .document(selectedStaffMember).collection(date)
            val querySnapshot = tableLayoutCollectionRef.get().await()

            val docRef = db.collection("restaurants").document(restaurant!!).collection("staff")
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

    private inner class ProductViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(
            view
        ) {

        fun setContent(documentId: String) {
            val documentIdTextView = view.findViewById<TextView>(com.example.seatpickerapp.R.id.tableTextView)

            when (documentId) {
                "tableOne" -> documentIdTextView.text = "Table One"
                "tableTwo" -> documentIdTextView.text = "Table Two"
                "tableThree" -> documentIdTextView.text = "Table Three"
                "tableFour" -> documentIdTextView.text = "Table Four"
                "tableFive" -> documentIdTextView.text = "Table Five"
                "tableSix" -> documentIdTextView.text = "Table Six"
                "tableSeven" -> documentIdTextView.text = "Table Seven"
                "tableEight" -> documentIdTextView.text = "Table Eight"
                "tableNine" -> documentIdTextView.text = "Table Nine"
                "tableTen" -> documentIdTextView.text = "Table Ten"
            }
            //documentIdTextView.text = documentId
        }

        fun itemSelected(documentId: String) {
            val tableCardView = view.findViewById<CardView>(com.example.seatpickerapp.R.id.tableCardView)

            tableCardView.setOnClickListener {
                validateData(documentId)
            }
        }

    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Tables>) :
        FirestoreRecyclerAdapter<Tables, StaffToTablesActivity.ProductViewHolder>(options) {
        override fun onBindViewHolder(
            productViewHolder: StaffToTablesActivity.ProductViewHolder,
            position: Int,
            tables: Tables
        ) {
            productViewHolder.setContent(snapshots.getSnapshot(position).id)
            productViewHolder.itemSelected(snapshots.getSnapshot(position).id)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffToTablesActivity.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(com.example.seatpickerapp.R.layout.item_table, parent, false)
            return ProductViewHolder(view)
        }
    }

}