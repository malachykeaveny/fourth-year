package com.example.seatpickerapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.seatpickerapp.dataClasses.ChatMessage
import com.example.seatpickerapp.dataClasses.UserContact
import com.example.seatpickerapp.databinding.ActivityMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class MessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding
    private var auth: FirebaseAuth? = null
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        val fromUserObject = intent.extras?.get("fromUser") as? UserContact
        val targetUserObject = intent.extras?.get("targetUser") as? UserContact
        //val targetUserId = intent.extras?.get("targetUserId") as String
        var roomId = intent.extras?.get("roomId") as String
        var fromUserRooms = fromUserObject?.rooms
        var targetUserRooms = targetUserObject?.rooms

        Log.d("MessageActivity", "$fromUserObject $targetUserObject $roomId")

        if (roomId == "") {
            roomId = db.collection("messages").document().id
            if (fromUserRooms != null) {
                for ((key, _) in fromUserRooms) {
                    if (targetUserRooms != null) {
                        if (targetUserRooms.contains(key)) {
                            roomId = key
                        }
                    }
                }
            }
        }

        binding.sendMessageBtn.setOnClickListener {

            if (fromUserRooms == null) {
                fromUserRooms = mutableMapOf()
            }
            fromUserRooms!![roomId] = true
            fromUserObject?.rooms = fromUserRooms
            db.collection("users").document(auth?.uid.toString()).set(fromUserObject!!, SetOptions.merge())
            db.collection("users").document(targetUserObject?.userId.toString()).collection("contacts").document(fromUserObject?.userId.toString()).set(fromUserObject!!, SetOptions.merge())
            db.collection("rooms").document(targetUserObject?.userId.toString()).collection("userRooms").document(roomId).set(fromUserObject, SetOptions.merge())

            if (targetUserRooms == null) {
                targetUserRooms = mutableMapOf()
            }
            targetUserRooms!![roomId] = true
            targetUserObject?.rooms = targetUserRooms
            db.collection("users").document(targetUserObject?.userId.toString()).set(targetUserObject!!, SetOptions.merge())
            db.collection("users").document(auth?.uid.toString()).collection("contacts").document(targetUserObject?.userId.toString()).set(targetUserObject!!, SetOptions.merge())
            db.collection("rooms").document(auth?.uid.toString()).collection("userRooms").document(roomId).set(targetUserObject, SetOptions.merge())

            val messageText = binding.messageBodyEditText.text.toString()
            val chatMessage = ChatMessage(messageText, auth?.uid.toString())
            db.collection("messages").document(roomId).collection("roomMessages").add(chatMessage)
            binding.messageBodyEditText.text.clear()
        }
    }
}