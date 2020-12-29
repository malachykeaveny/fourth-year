package com.example.seatpickerapp

public class Globals {

    companion object {
        fun convertTimeSlot(i: Int): String {
            when (i) {
                1 -> return("14:00 - 16:00")
                2 -> return("16:00 - 18:00")
                3 -> return("18:00 - 20:00")
                4 -> return("20:00 - 22:00")
            else -> return("Error in when statement")
                }
        }

        val TIME_SLOT_TOTAL = 4
        }
}