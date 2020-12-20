package com.example.seatpickerapp

import android.R
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.seatpickerapp.Fragments.RsrvStepOneFragment
import com.example.seatpickerapp.Fragments.RsrvStepThreeFragment
import com.example.seatpickerapp.Fragments.RsrvStepTwoFragment
import com.example.seatpickerapp.adapter.ViewPageAdapter
import com.example.seatpickerapp.databinding.ActivityReservationBinding
import com.shuhart.stepview.StepView


class ReservationActivity : AppCompatActivity() {
    //private lateinit var viewPager: ViewPager
    private lateinit var binding: ActivityReservationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //setContentView(R.layout.activity_reservation)

        binding.stepView.getState()
            .selectedTextColor(ContextCompat.getColor(this, R.color.holo_blue_light))
            .animationType(StepView.ANIMATION_CIRCLE)
            .selectedCircleColor(ContextCompat.getColor(this, R.color.holo_blue_light))
            //.selectedCircleRadius(resources.getDimensionPixelSize(R.dimen.dp14))
            .selectedStepNumberColor(
                ContextCompat.getColor(
                    this,
                    R.color.holo_red_light
                )
            ) // You should specify only stepsNumber or steps array of strings.
            // In case you specify both steps array is chosen.
            .steps(object : ArrayList<String?>() {
                init {
                    add("Rest")
                    add("Time")
                    add("Conf")
                }
            }) // You should specify only steps number or steps array of strings.
            // In case you specify both steps array is chosen.
            .stepsNumber(4)
            .animationDuration(resources.getInteger(R.integer.config_shortAnimTime))
            //.stepLineWidth(resources.getDimensionPixelSize(R.dimen.dp1))
            //.textSize(resources.getDimensionPixelSize(R.dimen.sp14))
            //.stepNumberTextSize(resources.getDimensionPixelSize(R.dimen.sp16))
            //.typeface(
              //  ResourcesCompat.getFont(
                //    context,
                  //  R.font.roboto_italic
                //)
            //) // other state methods are equal to the corresponding xml attributes
            .commit()

        addFragments()
    }

    private fun addFragments() {
        val adapter = ViewPageAdapter(supportFragmentManager)
        adapter.addFragment(RsrvStepOneFragment())
        adapter.addFragment(RsrvStepTwoFragment())
        adapter.addFragment(RsrvStepThreeFragment())
        //viewPager.adapter = adapter
        binding.viewPager.adapter = adapter

        var count = 0

        binding.backBtn.setOnClickListener {
            binding.viewPager.arrowScroll(View.FOCUS_LEFT)
            if (count > 0) {
                count -= 1
                binding.stepView.go(count, true)
            }
        }
        binding.nextBtn.setOnClickListener {
            binding.viewPager.arrowScroll(View.FOCUS_RIGHT)
            if (count < 3) {
                count +=1
                binding.stepView.go(count, true)
            }
        }
    }

    /*@SuppressLint("ResourceAsColor")
    private fun btnSetup() {
        if (back_btn!!.isEnabled)
            next_btn.setBackgroundColor(R.color.colorPrimaryDark)
        else
            back_btn.setBackgroundColor(R.color.colorPrimary)

        if (next_btn!!.isEnabled)
            next_btn.setBackgroundColor(R.color.colorPrimaryDark)
        else
            next_btn.setBackgroundColor(R.color.colorPrimary)

    } */

    private fun stepView() {
        var stepViewList = listOf("Restaurant", "Time", "Confirmation")
        binding.stepView.setSteps(stepViewList)
    }
}