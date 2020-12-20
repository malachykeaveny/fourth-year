package com.example.seatpickerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.seatpickerapp.Fragments.HomeFragment
import com.example.seatpickerapp.Fragments.ProfileFragment
import com.example.seatpickerapp.databinding.ActivityHomepageBinding

class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomepageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()
        setFragment(homeFragment)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.miHome -> setFragment(homeFragment)
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