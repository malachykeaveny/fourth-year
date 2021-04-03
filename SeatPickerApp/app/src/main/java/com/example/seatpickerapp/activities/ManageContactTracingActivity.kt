package com.example.seatpickerapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.seatpickerapp.R
import com.example.seatpickerapp.databinding.ActivityManageContactTracingBinding
import com.example.seatpickerapp.fragments.ManageContactTracingFragment
import java.util.*

class ManageContactTracingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageContactTracingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageContactTracingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val manageContactTracingFragment = ManageContactTracingFragment()
        setFragment(manageContactTracingFragment)
    }

    private fun setFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.manageCTFL, fragment)
            commit()
        }
}