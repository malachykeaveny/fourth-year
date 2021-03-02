package com.example.seatpickerapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.seatpickerapp.Login
import com.example.seatpickerapp.LoginActivity
import com.example.seatpickerapp.R
import com.example.seatpickerapp.databinding.FragmentHomeBinding
import com.example.seatpickerapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.StringBuilder


class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var auth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        getUser2()

        binding.btnLogout.setOnClickListener {
            logOut()
        }
    }

    private fun logOut() {
        auth?.signOut()
        startActivity(Intent(context, LoginActivity::class.java))
        Toast.makeText(context, "User has been logged out", Toast.LENGTH_SHORT)
            .show()
    }


    private fun getUser2() {
        lifecycleScope.launch {
            try {
                val userCollectionRef = Firebase.firestore.collection("users")
                val querySnapshot = userCollectionRef.get().await()
                for (document in querySnapshot.documents) {
                    Log.d("ProfileFragment", document.id + " " + auth?.uid.toString())
                    if (document.id == auth?.uid.toString()) {
                        val name = document.get("name").toString()
                        val email = document.get("emailAddress").toString()
                        val phoneNo = document.get("phoneNo").toString()
                        Log.d("ProfileFragment", name +  " " + email+ " " + phoneNo)
                        binding.tvUserNameTxtView.text = name
                        binding.tvUserEmailTxtView.text = email
                        binding.tvUserPhoneNoTxtView.text = phoneNo
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}