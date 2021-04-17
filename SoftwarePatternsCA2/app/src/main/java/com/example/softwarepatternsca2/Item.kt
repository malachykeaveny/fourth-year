package com.example.softwarepatternsca2

data class Item(
    var itemName: String = "",
    var category: String = "",
    var manufacturer: String = "",
    var price: Double = 0.0,
    var image: String = "",
    var stock: Int = 0,
    var reviewsTotalSum: Int = 0,
    var noOfReviews: Int = 0
)
