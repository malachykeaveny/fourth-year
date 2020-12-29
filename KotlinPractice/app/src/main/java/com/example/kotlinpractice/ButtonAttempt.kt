package com.example.kotlinpractice

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.kotlinpractice.databinding.ActivityButtonAttemptBinding
import com.example.kotlinpractice.databinding.ActivityReservationStepThreeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_button_attempt.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.StringBuilder

class ButtonAttempt : AppCompatActivity() {

    private lateinit var binding: ActivityButtonAttemptBinding
    val db = FirebaseFirestore.getInstance()
    val tableCollectionRef = db.collection("Tables").document("tableEight").collection("30.12.2020")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityButtonAttemptBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        getAvailableTimes()

    }

    private fun getAvailableTimes() = CoroutineScope(Dispatchers.IO).launch{
        try {
            val querySnapshot = tableCollectionRef.get().await()
            Log.d("ButtonAttempt", querySnapshot.size().toString())

            withContext(Dispatchers.Main) {

                for(document in querySnapshot.documents) {
                    if (document.id == "14.00")
                        button.isClickable = false
                        button.setBackgroundColor(Color.DKGRAY)
                        button.setTextColor(Color.WHITE)
                        button.append(" (Reserved) ")
                }
            }
        }
        catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ButtonAttempt, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}