package com.example.seatpickerapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.seatpickerapp.adapter.LoginViewPagerAdapter
import com.example.seatpickerapp.databinding.ActivityLogin2Binding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.GRAVITY_FILL

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogin2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogin2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.loginTabLayout.addTab(binding.loginTabLayout.newTab().setText("Login"))
        binding.loginTabLayout.addTab(binding.loginTabLayout.newTab().setText("Sign Up"))
        binding.loginTabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = LoginViewPagerAdapter(this, supportFragmentManager,
            binding.loginTabLayout.tabCount)
        binding.loginViewPager.adapter = adapter
        binding.loginViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.loginTabLayout))
        binding.loginTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.loginViewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.loginTabLayout.translationY = 300F
        binding.loginTabLayout.alpha = 0f
        binding.loginTabLayout.animate().translationY(0F).alpha(1F).setDuration(1000).setStartDelay(100).start()
    }
}