package com.example.seatpickerapp.dataClasses

import java.io.Serializable

data class UserContact(
    var name: String = "",
    var userId: String = "",
    var rooms: MutableMap<String, Any>?= null
) : Serializable