package com.example.seatpickerapp

interface LoadRestaurantListener {
    fun onRestaurantLoadSuccess(restaurantList: List<String?>?)
    fun onRestaurantLoadFailed(message: String?)
}