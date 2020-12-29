package com.example.seatpickerapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.graphics.red
import androidx.recyclerview.widget.RecyclerView
import com.example.seatpickerapp.Globals
import com.example.seatpickerapp.R
import com.example.seatpickerapp.ReservationTime
import com.example.seatpickerapp.databinding.LayoutTimesBinding
import java.lang.StringBuilder

class RevervationTimeAdapter(
    var reservationTimes: List<ReservationTime>
) : RecyclerView.Adapter<RevervationTimeAdapter.ReservationTimeViewHolder>() {


    inner class ReservationTimeViewHolder(val binding : LayoutTimesBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationTimeViewHolder {
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_times, parent, false)
        //return ReservationTimeViewHolder(view)

        val binding = LayoutTimesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReservationTimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReservationTimeViewHolder, position: Int) {
        holder.binding.timeTxt.setText(StringBuilder(Globals.convertTimeSlot(position)).toString())


        if (reservationTimes.isEmpty()) {
            holder.binding.timeAvailableTxt.text = "Available"
            holder.binding.layoutTime.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            //holder.binding.layoutTime.isEnabled
        }
        else {
            for (time: ReservationTime in reservationTimes) {

            }
        }
    }

    override fun getItemCount(): Int {
        return Globals.TIME_SLOT_TOTAL
    }
}