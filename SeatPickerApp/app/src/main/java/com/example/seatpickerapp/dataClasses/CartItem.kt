package com.example.seatpickerapp.dataClasses

data class CartItem(
    var itemName: String = "",
    var itemPrice: Double = 0.0,
    var image: String = "",
    var quantity: Int = 0
)
