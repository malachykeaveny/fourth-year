package com.example.seatpickerapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.seatpickerapp.fragments.BookingsFragment
import com.example.seatpickerapp.fragments.HomeFragment
import com.example.seatpickerapp.fragments.ProfileFragment
import com.example.seatpickerapp.R
import com.example.seatpickerapp.databinding.ActivityHomepageBinding
import com.example.seatpickerapp.fragments.OrdersFragment

class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomepageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()
        val bookingsFragment = BookingsFragment()
        val ordersFragment = OrdersFragment()
        setFragment(homeFragment)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.miHome -> setFragment(homeFragment)
                R.id.miBookings -> setFragment(bookingsFragment)
                R.id.miOrders -> setFragment(ordersFragment)
                R.id.miProfile -> setFragment(profileFragment)
            }
            true
        }
    }

    private fun setFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
}