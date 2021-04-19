package com.example.seatpickerapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.seatpickerapp.activities.HomePageActivity
import com.example.seatpickerapp.dataClasses.User
import com.example.seatpickerapp.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessaging
import java.util.HashMap


class SignUpFragment : Fragment() {

    private val TAG = "SignUpFragment"
    private var _binding: FragmentSignUpBinding?= null
    private val binding get() = _binding!!
    var db: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    private var name: String?= null
    private var email: String?= null
    private var password: String?= null
    private var phoneNo: String?= null
    private var userID: String? = null
    private var userToken: String?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.signupNameEt.translationX = 800F
        binding.signupEmailEt.translationX = 800F
        binding.signupPasswordEt.translationX = 800F
        binding.signupPhoneNoEt.translationX = 800F
        binding.signupBtn.translationX = 800F

        binding.signupNameEt.alpha = 0f
        binding.signupEmailEt.alpha = 0f
        binding.signupPasswordEt.alpha = 0f
        binding.signupPhoneNoEt.alpha = 0f
        binding.signupBtn.alpha = 0f

        binding.signupNameEt.animate().translationX(0F).alpha(1F).setDuration(800).setStartDelay(250).start()
        binding.signupEmailEt.animate().translationX(0F).alpha(1F).setDuration(800).setStartDelay(350).start()
        binding.signupPasswordEt.animate().translationX(0F).alpha(1F).setDuration(800).setStartDelay(450).start()
        binding.signupPhoneNoEt.animate().translationX(0F).alpha(1F).setDuration(800).setStartDelay(550).start()
        binding.signupBtn.animate().translationX(0F).alpha(1F).setDuration(800).setStartDelay(650).start()

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

        binding.signupBtn.setOnClickListener {
            if (binding.signupEmailEt.text.isEmpty()) {
                binding.signupEmailEt.setError("Email must not be empty.")
            }
            if (binding.signupPasswordEt.text.length < 7) {
                binding.signupPasswordEt.setError("Password must be at least 7 characters.")
            }

            name = binding.signupNameEt.text.toString()
            email = binding.signupEmailEt.text.toString()
            password = binding.signupPasswordEt.text.toString()
            phoneNo = binding.signupPhoneNoEt.text.toString()

            auth!!.createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "User has been created", Toast.LENGTH_SHORT).show()
                        userID = auth!!.currentUser!!.uid
                        val documentReference = db!!.collection("users").document(
                            userID!!
                        )
                        /*val user: MutableMap<String, Any> = HashMap()
                        user.put("name", name!!)
                        user.put("phoneNo", phoneNo!!)
                        user.put("emailAddress", email!!)
                        user.put("hasAdminPrivileges", false)
                         FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                            userToken = it.token
                        }
                         */

                        userToken = FirebaseInstanceId.getInstance().getToken().toString()

                        val user = User(name!!, email!!, phoneNo!!, false, userToken.toString())

                        Log.d("checkingToken", " token: $userToken")

                        //user.put("token", userToken.toString())

                        //user["name"] = name
                        //user["phoneNo"] = phoneNumber
                        //user["emailAddress"] = emailAddress

                        //user.put("rooms", MutableMap<String, Any>?= null)
                        documentReference.set(user).addOnSuccessListener {
                            Log.d(
                                TAG,
                                "user created for $userID"
                            )
                        }.addOnFailureListener { e ->
                            Log.d(
                                TAG,
                                "Firebase create user error:  $e"
                            )
                        }
                        startActivity(Intent(context, HomePageActivity::class.java))
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