package com.example.seatpickerapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.seatpickerapp.R
import com.example.seatpickerapp.ReservationTime
import com.example.seatpickerapp.adapter.RevervationTimeAdapter
import com.example.seatpickerapp.databinding.FragmentRsrvStepOneBinding
import com.example.seatpickerapp.databinding.FragmentRsrvStepTwoBinding


class RsrvStepTwoFragment : Fragment() {

    private var _binding: FragmentRsrvStepTwoBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        loadTimes()
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_rsrv_step_two, container, false)
        _binding = FragmentRsrvStepTwoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private fun loadTimes() {
        val adapter = RevervationTimeAdapter()
    }
}