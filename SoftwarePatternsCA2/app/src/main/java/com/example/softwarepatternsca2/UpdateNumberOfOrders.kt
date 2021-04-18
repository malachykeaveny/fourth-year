package com.example.softwarepatternsca2

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpdateNumberOfOrders : Command {

    private val db = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun execute(user: User) {
        val userDocRef = db.collection("users").document(auth?.uid.toString())
        userDocRef
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    var numberOfOrders = document.get("numberOfOrders").toString().toInt()
                    Log.d("CartActivity", "DocumentSnapshot data: ${document.data}")

                    userDocRef
                        .update("numberOfOrders", numberOfOrders + 1)
                        .addOnSuccessListener { Log.d("CartActivity", "DocumentSnapshot successfully updated!") }
                        .addOnFailureListener { e -> Log.w("CartActivity", "Error updating document", e) }

                } else {
                    Log.d("CartActivity", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("CartActivity", "get failed with ", exception)
            }
    }

}