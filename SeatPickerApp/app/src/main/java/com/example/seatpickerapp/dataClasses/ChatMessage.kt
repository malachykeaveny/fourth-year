package com.example.seatpickerapp.dataClasses

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class ChatMessage (
    var messageText: String = "",
    var fromUserId: String = "",
    @ServerTimestamp
    var timeSentAt: Date?= null
)