package com.example.kotlinpractice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_reservation_step_two.*

class ReservationStepTwo: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_step_two)
        val tableNumber = intent.getStringExtra("tableNumber")

        textView.text = tableNumber
    }
}