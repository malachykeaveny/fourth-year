package com.example.seatpickerapp.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.seatpickerapp.R
import com.example.seatpickerapp.activities.HomePageActivity
import com.example.seatpickerapp.activities.TOPIC
import com.example.seatpickerapp.dataClasses.Booking
import com.example.seatpickerapp.databinding.FragmentOakFirePizzaBinding
import com.example.seatpickerapp.firebaseNotifications.NotificationData
import com.example.seatpickerapp.firebaseNotifications.PushNotification
import com.example.seatpickerapp.firebaseNotifications.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*


class OakFirePizzaFragment : Fragment() {

    private var _binding: FragmentOakFirePizzaBinding?= null
    private val binding get() = _binding!!
    private val TAG = "OakFirePizzaFragment"
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
    private var tableNineSelected: Boolean = false
    private var tableTenSelected: Boolean = false
    private var canSelectMultipleTables: Boolean = false
    private var specialLayout: String = "default"
    private var tableOneSeats: Int = 2
    private var tableTwoSeats: Int = 2
    private var tableThreeSeats: Int = 2
    private var tableFourSeats: Int = 2
    private var tableFiveSeats: Int = 2
    private var tableSixSeats: Int = 2
    private var tableSevenSeats: Int = 2
    private var tableEightSeats: Int = 2
    private var tableNineSeats: Int = 2
    private var tableTenSeats: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOakFirePizzaBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setPartySize()

        binding.partySizeBtn.setOnClickListener {
            setPartySize()
        }

        binding.selectDateBtn.setOnClickListener {
            setDate()
        }

        binding.twoPmBtn.setOnClickListener {
            deSelectTables()
            getReservedTables(date!!, "15.00")
            time = "15.00"
            when (canSelectMultipleTables) {
                false -> getUnsuitableTables(partySize!!)
            }
        }

        binding.fourPmBtn.setOnClickListener {
            deSelectTables()
            getReservedTables(date!!, "17.00")
            time = "17.00"
            when (canSelectMultipleTables) {
                false -> getUnsuitableTables(partySize!!)
            }
        }

        binding.sixPmBtn.setOnClickListener {
            deSelectTables()
            getReservedTables(date!!, "19.00")
            time = "19.00"
            when (canSelectMultipleTables) {
                false -> getUnsuitableTables(partySize!!)
            }
        }

        binding.eightPmBtn.setOnClickListener {
            deSelectTables()
            getReservedTables(date!!, "21.00")
            time = "21.00"
            when (canSelectMultipleTables) {
                false -> getUnsuitableTables(partySize!!)
            }
        }

        binding.ivTableOne.setOnClickListener {

            if (date == null || time == null || partySize == null) {
                Toast.makeText(context, "Pick a date, time and party size first", Toast.LENGTH_SHORT)
                    .show()
            } else {
                //bookingDialog(date!!, time!!, "tableOne")
                when (canSelectMultipleTables) {
                    false -> deSelectTables()
                }
                when (specialLayout) {
                    "default" -> binding.ivTableOne.setImageResource(R.drawable.ic_two_seater_dark_blue)
                }
                tableOneSelected = true
            }
        }

        binding.ivTableTwo.setOnClickListener {

            if (date == null || time == null || partySize == null) {
                Toast.makeText(context, "Pick a date, time and party size first", Toast.LENGTH_SHORT)
                    .show()
            } else {
                //bookingDialog(date!!, time!!, "tableTwo")
                when (canSelectMultipleTables) {
                    false -> deSelectTables()
                }
                when (specialLayout) {
                    "default" -> binding.ivTableTwo.setImageResource(R.drawable.ic_two_seater_dark_blue)
                }
                tableTwoSelected = true
            }
        }

        binding.ivTableThree.setOnClickListener {

            if (date == null || time == null || partySize == null) {
                Toast.makeText(context, "Pick a date, time and party size first", Toast.LENGTH_SHORT).show()
            } else {
                when (canSelectMultipleTables) {
                    false -> deSelectTables()
                }
                when (specialLayout) {
                    "default" -> binding.ivTableThree.setImageResource(R.drawable.ic_two_seater_dark_blue)
                }
                tableThreeSelected = true
            }
        }

        binding.ivTableFour.setOnClickListener {

            if (date == null || time == null || partySize == null) {
                Toast.makeText(context, "Pick a date, time and party size first", Toast.LENGTH_SHORT).show()
            } else {
                when (canSelectMultipleTables) {
                    false -> deSelectTables()
                }
                when (specialLayout) {
                    "default" -> binding.ivTableFour.setImageResource(R.drawable.ic_two_seater_dark_blue)
                }
                tableFourSelected = true
            }
        }

        binding.ivTableFive.setOnClickListener {

            if (date == null || time == null || partySize == null) {
                Toast.makeText(context, "Pick a date, time and party size first", Toast.LENGTH_SHORT).show()
            } else {
                //bookingDialog(date!!, time!!, "tableFive")
                when (canSelectMultipleTables) {
                    false -> deSelectTables()
                }
                when (specialLayout) {
                    "default" -> binding.ivTableFive.setImageResource(R.drawable.ic_two_seater_dark_blue)
                }
                tableFiveSelected = true
            }
        }

        binding.ivTableSix.setOnClickListener {

            if (date == null || time == null || partySize == null) {
                Toast.makeText(context, "Pick a date, time and party size first", Toast.LENGTH_SHORT).show()
            } else {
                //bookingDialog(date!!, time!!, "tableSix")
                when (canSelectMultipleTables) {
                    false -> deSelectTables()
                }
                when (specialLayout) {
                    "default" -> binding.ivTableSix.setImageResource(R.drawable.ic_two_seater_dark_blue)
                }
                tableSixSelected = true
            }
        }

        binding.ivTableSeven.setOnClickListener {

            if (date == null || time == null || partySize == null) {
                Toast.makeText(
                    context,
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
                    "default" -> binding.ivTableSeven.setImageResource(R.drawable.ic_two_seater_dark_blue)
                }
                tableSevenSelected = true
            }
        }

        binding.ivTableEight.setOnClickListener {
            if (date == null || time == null || partySize == null) {
                Toast.makeText(
                    context,
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
                    "default" -> binding.ivTableEight.setImageResource(R.drawable.ic_two_seater_dark_blue)
                }
                tableEightSelected = true
            }
        }

        binding.ivTableNine.setOnClickListener {
            if (date == null || time == null || partySize == null) {
                Toast.makeText(
                    context,
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
                    "default" -> binding.ivTableNine.setImageResource(R.drawable.ic_two_seater_dark_blue)
                }
                tableEightSelected = true
            }
        }

        binding.ivTableTen.setOnClickListener {
            if (date == null || time == null || partySize == null) {
                Toast.makeText(
                    context,
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
                    "default" -> binding.ivTableTen.setImageResource(R.drawable.ic_two_seater_dark_blue)
                }
                tableEightSelected = true
            }
        }

        binding.bookBtn.setOnClickListener {
            when {
                date == null || time == null || partySize == null -> Toast.makeText(context, "Pick a date, time and party size first", Toast.LENGTH_SHORT).show()
                //!tableOneSelected && !tableTwoSelected && !tableThreeSelected && !tableFourSelected && !tableFiveSelected && !tableSixSelected && !tableSevenSelected && !tableEightSelected
                tableOneSelected == false && tableTwoSelected == false && tableThreeSelected == false && tableFourSelected == false && tableFiveSelected == false &&
                        tableSixSelected == false && tableSevenSelected == false && tableEightSelected == false && tableNineSelected == false && tableTenSelected == false
                -> Toast.makeText(context, "Please select at least one table", Toast.LENGTH_SHORT).show()
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
                    if (tableNineSelected)
                        sb.append("tableNine")
                    if (tableTenSelected)
                        sb.append("tableTen")

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
                        tableNineSelected -> bookingDialog(date!!, time!!, "tableNine")
                        tableTenSelected -> bookingDialog(date!!, time!!, "tableTen")
                    }
                }
            }
        }

    }

    private fun setPartySize() {
        val seatList = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        val partySizeDialog = AlertDialog.Builder(requireContext())
            .setTitle("How many people are in you booking for?")
            .setSingleChoiceItems(seatList, -1) { dialogInterface, i -> }
            .setPositiveButton("Ok") { dialog, which ->
                val position = (dialog as AlertDialog).listView.checkedItemPosition

                if (position != -1) {
                    partySize = seatList[position]
                    //Toast.makeText(this, "You picked: $partySize", Toast.LENGTH_SHORT).show()
                    if (partySize!!.toInt() > 2) {
                        Toast.makeText(context, "Party size:  $partySize", Toast.LENGTH_SHORT).show()
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

    private fun setDate() {
        val dpd =
            DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                var month = mMonth + 1
                date = "$mDay.$month.$mYear"
                binding.selectDateBtn.setText(date)

                getTableLayout()

                binding.twoPmBtn.visibility = Button.VISIBLE
                binding.fourPmBtn.visibility = Button.VISIBLE
                binding.sixPmBtn.visibility = Button.VISIBLE
                binding.eightPmBtn.visibility = Button.VISIBLE

            }, year, month, day)
        dpd.datePicker.minDate = System.currentTimeMillis() - 1000
        dpd.show()
    }

    private fun getTableLayout() {
        val tableLayoutDocRef = db.collection("restaurants").document("oakFirePizza").collection("tableLayouts").document(date!!)
        tableLayoutDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    //Toast.makeText(context, "A special layout already exists for $date !", Toast.LENGTH_SHORT).show()
                    Log.d("FlanagansFragment", "DocumentSnapshot data: ${document.data}")
                    binding.ivTableOne.isVisible = document.get("tableOneVisible").toString().toBoolean()
                    binding.ivTableTwo.isVisible = document.get("tableTwoVisible").toString().toBoolean()
                    binding.ivTableThree.isVisible = document.get("tableThreeVisible").toString().toBoolean()
                    binding.ivTableFour.isVisible = document.get("tableFourVisible").toString().toBoolean()
                    binding.ivTableFive.isVisible = document.get("tableFiveVisible").toString().toBoolean()
                    binding.ivTableSix.isVisible = document.get("tableSixVisible").toString().toBoolean()
                    binding.ivTableSeven.isVisible = document.get("tableSevenVisible").toString().toBoolean()
                    binding.ivTableEight.isVisible = document.get("tableEightVisible").toString().toBoolean()
                    binding.ivTableNine.isVisible = document.get("tableNineVisible").toString().toBoolean()
                    binding.ivTableTen.isVisible = document.get("tableTenVisible").toString().toBoolean()

                } else {
                    Log.d("FlanagansFragment", "No such document")
                    binding.ivTableOne.isVisible = true
                    binding.ivTableTwo.isVisible = true
                    binding.ivTableThree.isVisible = true
                    binding.ivTableFour.isVisible = true
                    binding.ivTableFive.isVisible = true
                    binding.ivTableSix.isVisible = true
                    binding.ivTableSeven.isVisible = true
                    binding.ivTableEight.isVisible = true
                    binding.ivTableNine.isVisible = true
                    binding.ivTableTen.isVisible = true
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ChangeSeatingActivity", "get failed with ", exception)
            }
    }

    private fun deSelectTables() {

        if (tableOneSelected) {
            tableOneSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableOne.setImageResource(R.drawable.ic_two_seater_light_blue)
            }
        }
        if (tableTwoSelected) {
            tableTwoSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableTwo.setImageResource(R.drawable.ic_two_seater_light_blue)
            }
        }
        if (tableThreeSelected) {
            tableThreeSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableThree.setImageResource(R.drawable.ic_two_seater_light_blue)
            }
        }
        if (tableFourSelected) {
            tableFourSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableFour.setImageResource(R.drawable.ic_two_seater_light_blue)
            }
        }
        if (tableFiveSelected) {
            tableFiveSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableFive.setImageResource(R.drawable.ic_two_seater_light_blue)
            }
        }
        if (tableSixSelected) {
            tableSixSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableSix.setImageResource(R.drawable.ic_two_seater_light_blue)
            }
        }
        if (tableSevenSelected) {
            tableSevenSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableSeven.setImageResource(R.drawable.ic_two_seater_light_blue)
            }
        }
        if (tableEightSelected) {
            tableEightSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableEight.setImageResource(R.drawable.ic_two_seater_light_blue)
            }
        }
        if (tableNineSelected) {
            tableNineSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableEight.setImageResource(R.drawable.ic_two_seater_light_blue)
            }
        }

        if (tableTenSelected) {
            tableTenSelected = false
            when (specialLayout) {
                "default" -> binding.ivTableTen.setImageResource(R.drawable.ic_two_seater_light_blue)
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
                "tableEight",
                "tableNine",
                "tableTen"
            )
            for (i in tables) {
                val tableCollectionRef =
                    db.collection("restaurants").document("oakFirePizza").collection("tables")
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
                                                "default" -> binding.ivTableFour.setImageResource(R.drawable.ic_two_seater_black)
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
                                                "default" -> binding.ivTableFive.setImageResource(R.drawable.ic_two_seater_black)
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
                                                "default" -> binding.ivTableSix.setImageResource(R.drawable.ic_two_seater_black)
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
                                                "default" -> binding.ivTableSeven.setImageResource(R.drawable.ic_two_seater_black)
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
                                                "default" -> binding.ivTableEight.setImageResource(R.drawable.ic_two_seater_black)
                                            }
                                            binding.ivTableEight.isClickable = false
                                        }.start()
                                    }
                                    "tableNine" -> {
                                        binding.ivTableEight.animate().apply {
                                            duration = 750
                                            rotationXBy(180f)
                                        }.withEndAction {
                                            when (specialLayout) {
                                                "default" -> binding.ivTableNine.setImageResource(R.drawable.ic_two_seater_black)
                                            }
                                            binding.ivTableNine.isClickable = false
                                        }.start()
                                    }
                                    "tableTen" -> {
                                        binding.ivTableTen.animate().apply {
                                            duration = 750
                                            rotationXBy(180f)
                                        }.withEndAction {
                                            when (specialLayout) {
                                                "default" -> binding.ivTableTen.setImageResource(R.drawable.ic_two_seater_black)
                                            }
                                            binding.ivTableTen.isClickable = false
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
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT)
                            .show()
                        Log.d("getAvailableTables", e.message.toString())
                    }
                }
            }
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
                "tableEight",
                "tableNine",
                "tableTen"
            )
            for (i in tables) {
                try {
                    val seatDocRef =
                        db.collection("restaurants").document("oakFirePizza").collection("tables")
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
                                            "default" -> binding.ivTableFour.setImageResource(R.drawable.ic_two_seater_grey)
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
                                            "default" -> binding.ivTableFive.setImageResource(R.drawable.ic_two_seater_grey)
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
                                            "default" -> binding.ivTableSix.setImageResource(R.drawable.ic_two_seater_grey)
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
                                            "default" -> binding.ivTableSeven.setImageResource(R.drawable.ic_two_seater_grey)
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
                                            "default" -> binding.ivTableEight.setImageResource(R.drawable.ic_two_seater_grey)
                                        }
                                        binding.ivTableEight.isClickable = false
                                    }.start()

                                }
                            }
                            "tableNine" -> {
                                if (partySize.toInt() > tableNineSeats) {
                                    binding.ivTableNine.animate().apply {
                                        duration = 750
                                        rotationXBy(180f)
                                    }.withEndAction {
                                        when (specialLayout) {
                                            "default" -> binding.ivTableNine.setImageResource(R.drawable.ic_two_seater_grey)
                                        }
                                        binding.ivTableNine.isClickable = false
                                    }.start()

                                }
                            }
                            "tableTen" -> {
                                if (partySize.toInt() > tableTenSeats) {
                                    binding.ivTableTen.animate().apply {
                                        duration = 750
                                        rotationXBy(180f)
                                    }.withEndAction {
                                        when (specialLayout) {
                                            "default" -> binding.ivTableTen.setImageResource(R.drawable.ic_two_seater_grey)
                                        }
                                        binding.ivTableTen.isClickable = false
                                    }.start()

                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT)
                            .show()
                        Log.d("getAvailableTables", e.message.toString())
                    }
                }
            }
        }

    private fun bookingDialog(date: String, time: String, tableNo: String) {
        val builder = AlertDialog.Builder(requireActivity())
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
            "tableNine" -> tableLong = "9"
            "tableTen" -> tableLong = "10"
        }
        builder.setMessage("Date: " + date + "\nTime: " + time + "\nTable Number: " + tableLong)
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            createBooking(date!!, time!!, tableNo)
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(
                context,
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
                "tableNine" -> sb.append("9, ")
                "tableTen" -> sb.append("10, ")
            }
        }


        val builder = AlertDialog.Builder(requireActivity())
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
                context,
                android.R.string.no, Toast.LENGTH_SHORT
            ).show()
        }

        builder.show()
    }

    private fun askToBookMultipleTables(partySize: String) {
        val multipleTableDialog = AlertDialog.Builder(requireContext())
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

    private fun setTablesAvailable() {
        when (specialLayout) {
            "default" -> binding.ivTableOne.setImageResource(R.drawable.ic_two_seater_light_blue)
        }
        binding.ivTableOne.isClickable = true

        when (specialLayout) {
            "default" -> binding.ivTableTwo.setImageResource(R.drawable.ic_two_seater_light_blue)
        }
        binding.ivTableTwo.isClickable = true


        when (specialLayout) {
            "default" -> binding.ivTableThree.setImageResource(R.drawable.ic_two_seater_light_blue)
        }
        binding.ivTableThree.isClickable = true

        when (specialLayout) {
            "default" -> binding.ivTableFour.setImageResource(R.drawable.ic_two_seater_light_blue)
        }
        binding.ivTableFour.isClickable = true

        when (specialLayout) {
            "default" -> binding.ivTableFive.setImageResource(R.drawable.ic_two_seater_light_blue)
        }
        binding.ivTableFive.isClickable = true

        when (specialLayout) {
            "default" -> binding.ivTableSix.setImageResource(R.drawable.ic_two_seater_light_blue)
        }
        binding.ivTableSix.isClickable = true

        when (specialLayout) {
            "default" -> binding.ivTableSeven.setImageResource(R.drawable.ic_two_seater_light_blue)
        }
        binding.ivTableSeven.isClickable = true

        when (specialLayout) {
            "default" -> binding.ivTableEight.setImageResource(R.drawable.ic_two_seater_light_blue)
        }
        binding.ivTableEight.isClickable = true

        when (specialLayout) {
            "default" -> binding.ivTableNine.setImageResource(R.drawable.ic_two_seater_light_blue)
        }
        binding.ivTableNine.isClickable = true

        when (specialLayout) {
            "default" -> binding.ivTableTen.setImageResource(R.drawable.ic_two_seater_light_blue)
        }
        binding.ivTableTen.isClickable = true
    }

    private fun createBooking(date: String, time: String, tableNo: String) =
        CoroutineScope(Dispatchers.IO).launch {

            val userDocRef = db.collection("users").document(auth?.currentUser?.uid.toString())
            val docSnapshot = userDocRef.get().await()
            Log.d("userDoc", docSnapshot.get("name").toString())

            val tableCollectionRef =
                db.collection("restaurants").document("oakFirePizza").collection("tables").document(
                    tableNo
                ).collection(date)
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
                    context,
                    "Booking created!",
                    Toast.LENGTH_SHORT
                ).show()
                getReservedTables(date, time)
                //personCollectionRef.document("Uykv5wvCCEcuIARFQ6hx").update("booking", "2pm 15th Jan")

                val userBooking = Booking("Oak Fire Pizza", date, time, tableNo)
                personCollectionRef.document(auth?.uid.toString()).collection("booking")
                    .add(userBooking)
                    .addOnSuccessListener { Log.d(TAG, "User updated with booking!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating user with booking", e) }

                val adminCollectionRef =
                    db.collection("restaurants").document("oakFirePizza").collection(
                        "bookingsMgmt"
                    ).document("tableBookings").collection(date)
                val adminBooking = hashMapOf(
                    "date" to date,
                    "time" to time,
                    "tableNo" to tableNo,
                    "name" to docSnapshot.get("name").toString(),
                    "phoneNo" to docSnapshot.get("phoneNo").toString(),
                    "email" to docSnapshot.get("emailAddress").toString()
                )
                adminCollectionRef.add(adminBooking)
                    .addOnSuccessListener { Log.d(TAG, "Admin updated with booking!") }
                    .addOnFailureListener { e ->
                        Log.w(
                            TAG,
                            "Error updating admin collection with booking",
                            e
                        )
                    }

                notifyAdmin(date, time, tableNo)

                //Log.d(TAG, adminRef.toString())
                startActivity(Intent(context, HomePageActivity::class.java))


            }.addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
                Toast.makeText(
                    context,
                    e.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

            Log.d("checkIfDateExists", date + " " + tableNo + " " + querySnapshot.size().toString())
        }

    private fun notifyAdmin(date: String, time: String, tableNo: String) {
        val docRef = db.collection("users").document("R8Bn0SsH9SNSZyGxhPrU1BDN3iL2")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {

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
                        "tableNine" -> tableLong = "9"
                        "tableTen" -> tableLong = "10"
                    }

                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    Log.d("reportTokenCheck", document.get("token").toString())
                    FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
                    val title = "Oak Fire Pizza: Table reservation received"
                    val message =
                        "Table $tableLong has been booked for $time on $date"
                    val recipientToken = document.get("token").toString()
                    PushNotification(
                        NotificationData(title, message),
                        recipientToken
                    ).also {
                        sendNotification(it)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful) {
                Log.d("OakFirePizzaFragment", "Response; ${Gson().toJson(response)}")
            }
            else {
                Log.e("OakFirePizzaFragment", response.errorBody().toString())
            }
        } catch (e: java.lang.Exception) {
            Log.e("OakFirePizzaFragment", e.toString())
        }
    }



}