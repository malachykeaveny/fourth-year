package com.example.seatpickerapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.seatpickerapp.activities.AdminHomeActivity
import com.example.seatpickerapp.activities.HomePageActivity
import com.example.seatpickerapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding?= null
    private val binding get() = _binding!!
    var db: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    private var email: String?= null
    private var password: String?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.loginEmailEt.translationX = 800F
        binding.loginPasswordEt.translationX = 800F
        binding.loginBtn.translationX = 800F

        binding.loginEmailEt.alpha = 0f
        binding.loginPasswordEt.alpha = 0f
        binding.loginBtn.alpha = 0f

        binding.loginEmailEt.animate().translationX(0F).alpha(1F).setDuration(800).setStartDelay(250).start()
        binding.loginPasswordEt.animate().translationX(0F).alpha(1F).setDuration(800).setStartDelay(350).start()
        binding.loginBtn.animate().translationX(0F).alpha(1F).setDuration(800).setStartDelay(450).start()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.loginBtn.setOnClickListener {
            if (binding.loginEmailEt.text.isEmpty()) {
                binding.loginEmailEt.setError("Email must not be empty.")
            } else if (binding.loginPasswordEt.text.length < 7) {
                binding.loginPasswordEt.setError("Password must be at least 7 characters.")
            } else {
                email = binding.loginEmailEt.text.toString()
                password = binding.loginPasswordEt.text.toString()

                //sign in user
                auth!!.signInWithEmailAndPassword(email!!, password!!)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            checkIfAdmin()
                        } else {
                            Toast.makeText(
                                context,
                                "Error: " + task.exception!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }


    }

    private fun checkIfAdmin() {
        lifecycleScope.launch {
            try {
                val userCollectionRef = Firebase.firestore.collection("users")
                val querySnapshot = userCollectionRef.get().await()
                for (document in querySnapshot.documents) {
                    Log.d("adminUid", document.id + " " + auth?.uid.toString())
                    if (document.id == auth?.uid.toString()) {
                        val hasAdminPrivileges = document.get("hasAdminPrivileges")
                        Log.d("hasAdminPriv", hasAdminPrivileges.toString())
                        when (hasAdminPrivileges) {
                            true -> startActivity(Intent(context, AdminHomeActivity::class.java))
                            false -> startActivity(Intent(context, HomePageActivity::class.java))
                        }
                    }
                }
            }
            catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}