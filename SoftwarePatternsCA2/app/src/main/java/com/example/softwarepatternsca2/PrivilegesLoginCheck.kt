package com.example.softwarepatternsca2

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PrivilegesLoginCheck : Privileges{

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun hasAdminPrivileges(): Boolean {

        var hasPrivileges: Boolean = true

        var userCollectionRef = db.collection("users")
        userCollectionRef.get().addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    if (document.id == auth?.uid.toString()) {
                        val hasAdminPrivileges = document.get("hasAdminPrivileges")
                        Log.d("hasAdminPriv", hasAdminPrivileges.toString())
                        when (hasAdminPrivileges) {
                            true -> hasPrivileges = true
                            false -> hasPrivileges = false
                        }
                    }
                }
            }
        })

        Log.d("PrivilegesLoginCheck", hasPrivileges.toString())
        return  hasPrivileges
    }
}