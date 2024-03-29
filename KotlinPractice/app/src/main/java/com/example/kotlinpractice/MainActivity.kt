package com.example.kotlinpractice

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val docref = db.collection("Tables").document("tableOne")
    private val personCollectionRef = Firebase.firestore.collection("users")
    private val tableCollectionRef = Firebase.firestore.collection("Tables").document("tableOne").collection("Monday")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnUploadData.setOnClickListener {
            val firstName  = etFirstName.text.toString()
            val secondName = etLastName.text.toString()
            val phoneNumber = etPhoneNumber.text.toString()
            val user = User(firstName, secondName, phoneNumber)
            saveUser(user)
        }

        //subscribeToRealtimeUpdates()

        btnRetrieveData.setOnClickListener {
            subscribeToRealtimeUpdatesTable()
        }
    }

    private fun subscribeToRealtimeUpdates() {
        personCollectionRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            querySnapshot?.let {
                val sb = StringBuilder()
                for(document in it) {
                    val user = document.toObject<User>()
                    sb.append("$user\n")
                }
                tvUsers.text = sb.toString()
            }
        }
    }

    private fun subscribeToRealtimeUpdatesTable() {
        tableCollectionRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            querySnapshot?.let {
                val sb = StringBuilder()
                for(document in it) {
                    val table = document.id + " reserved: " + document.get("reserved")
                    sb.append("$table\n")
                }
                tvUsers.text = sb.toString()
            }
        }
    }

    private fun retrieveUsers() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapshot = personCollectionRef.get().await()
            val sb = StringBuilder()
            for(document in querySnapshot.documents) {
                val user = document.toObject<User>()
                sb.append("$user\n")
            }
            withContext(Dispatchers.Main) {
                tvUsers.text = sb.toString()
            }
        }
        catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUser(user: User) = CoroutineScope(Dispatchers.IO).launch {
        try {
            personCollectionRef.add(user).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Successfully saved user", Toast.LENGTH_SHORT).show()
            }
        }
        catch(e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun retrieveTables() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapshot = tableCollectionRef.get().await()
            //val querySnapshot = tableCollectionRef.document()
            val sb = StringBuilder()
            for(document in querySnapshot.documents) {
                //val table = document.toObject<Day>()
                val table = document.id + " reserved: " + document.get("reserved")
                sb.append("$table\n")
                }
            withContext(Dispatchers.Main) {
                tvUsers.text = sb.toString()
            }
        }
        catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}