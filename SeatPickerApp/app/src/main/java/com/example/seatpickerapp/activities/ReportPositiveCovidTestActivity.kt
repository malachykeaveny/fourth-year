package com.example.seatpickerapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.seatpickerapp.fragments.ReportPositiveTestFragment
import com.example.seatpickerapp.R
import com.example.seatpickerapp.databinding.ActivityReportPositiveCovidTestBinding
import com.example.seatpickerapp.firebaseNotifications.PushNotification
import com.example.seatpickerapp.firebaseNotifications.RetrofitInstance
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

const val TOPIC = "s/topics/myTopic"

class ReportPositiveCovidTestActivity : AppCompatActivity() {

    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    private val db = FirebaseFirestore.getInstance()
    private var date: String? = null
    private var time: String? = null
    private lateinit var binding: ActivityReportPositiveCovidTestBinding
    val TAG = "ReportPositiveCovidTest"
    private var myToken: String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportPositiveCovidTestBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        //setDate()

        val reportPositiveTestFragment = ReportPositiveTestFragment()
        setFragment(reportPositiveTestFragment)

        /**FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            //FirebaseService.token = it.token
            myToken = it.token
            binding.textView.setText(myToken)
            Log.d("checkingToken", myToken.toString())
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        binding.btnSend.setOnClickListener {
            val title = "Covid Title"
            val message = "Positive Covid-19 test reported"
            val recipientToken = myToken
            PushNotification(
                NotificationData(title, message),
                recipientToken!!
            ).also {
                sendNotification(it)
            }
        }
        */
    }

    private fun setFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragmentReport, fragment)
            commit()
        }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful) {
                Log.d(TAG, "Response; ${Gson().toJson(response)}")
            }
            else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}