package com.example.seatpickerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.seatpickerapp.R
import com.example.seatpickerapp.databinding.FragmentOakFirePizzaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
    private var specialLayout: String = ""
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
    }


}