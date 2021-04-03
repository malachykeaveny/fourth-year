package com.example.seatpickerapp.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.seatpickerapp.fragments.LoginFragment
import com.example.seatpickerapp.fragments.SignUpFragment

@Suppress("DEPRECATION")
class LoginViewPagerAdapter(
    var context: Context,
    fm: FragmentManager,
    var totalTabs: Int
) : FragmentPagerAdapter(fm){

    override fun getCount(): Int {
        return totalTabs
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> LoginFragment()
            1 -> SignUpFragment()
            else -> getItem(position)
        }
    }

}