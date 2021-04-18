package com.example.softwarepatternsca2

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddUser : Command {

    private val db = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun execute(user: User) {
        val documentReference = db.collection("users").document(auth?.uid.toString())
        documentReference.set(user).addOnSuccessListener {
            Log.d("SignUpActivity", "user created for $auth?.uid.toString()")
        }.addOnFailureListener { e ->
            Log.d("SignUpActivity", "Firebase create user error:  $e")
        }
    }

}