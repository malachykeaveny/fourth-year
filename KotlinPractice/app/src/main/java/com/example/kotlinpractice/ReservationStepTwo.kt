package com.example.kotlinpractice

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_reservation_step_two.*
import java.util.*

class ReservationStepTwo: AppCompatActivity() {

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_step_two)
        val tableNumber = intent.getStringExtra("tableNumber")

        tvTableNo.setText("Pick a date for table" + tableNumber)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        pickDateBtn.setOnClickListener{
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{view, mYear, mMonth, mDay ->
                tvDate.setText(""+ mDay +"/" + mMonth + "/"+mYear)

            }, year, month, day)
            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()
    }
    }
}