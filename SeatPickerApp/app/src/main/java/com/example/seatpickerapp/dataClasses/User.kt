package com.example.seatpickerapp.dataClasses

import java.io.Serializable

data class User(
    var name: String = "",
    var emailAddress: String = "",
    var phoneNo: String = "",
    var hasAdminPrivileges: Boolean = false,
    var token: String = "",
    var rooms: MutableMap<String, Any>?= null,
    var userId: String = ""
) : Serializable
