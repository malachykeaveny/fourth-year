package com.example.softwarepatternsca2

data class User(
    var name: String = "",
    var emailAddress: String = "",
    var phoneNo: String = "",
    var hasAdminPrivileges: Boolean = false,
    var numberOfOrders: Int = 0
)
