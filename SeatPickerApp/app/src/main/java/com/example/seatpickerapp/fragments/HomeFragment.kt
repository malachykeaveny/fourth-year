package com.example.seatpickerapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.seatpickerapp.*
import com.example.seatpickerapp.activities.BookingActivity
import com.example.seatpickerapp.activities.OrderFoodActivity
import com.example.seatpickerapp.activities.ReportPositiveCovidTestActivity
import com.example.seatpickerapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception


class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var auth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding.bookingLayout.setOnClickListener {
            startActivity(Intent(context, BookingActivity::class.java))
        }

        binding.reportLayout.setOnClickListener {
            startActivity(Intent(context, ReportPositiveCovidTestActivity::class.java))
        }

        binding.foodLayout.setOnClickListener {
            startActivity(Intent(context, OrderFoodActivity::class.java))
        }

        lifecycleScope.launch {
            try {
                val userCollectionRef = Firebase.firestore.collection("users")
                val querySnapshot = userCollectionRef.get().await()
                for (document in querySnapshot.documents) {
                    if (document.id == auth?.uid.toString()) {
                        val name = document.get("name").toString()
                        binding.helloTxtView.text = "Hello ${name.split(" ")[0]}"
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}