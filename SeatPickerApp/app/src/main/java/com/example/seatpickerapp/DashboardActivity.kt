package com.example.seatpickerapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.example.seatpickerapp.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class DashboardActivity : AppCompatActivity(), View.OnClickListener {

    var db: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    lateinit var toggle: ActionBarDrawerToggle
    var layout: ViewGroup? = null
    var tables = ("E___T___/"
            + "_A__U__A/"
            + "________/"
            + "B_A__U__/"
            + "_________b/"
            + "_A__U__A/"
            )
    var tableViewList: MutableList<TextView> = ArrayList()
    var tableSize = 220
    var tableSpacing = 5
    var STATUS_AVAILABLE = 1
    var STATUS_BOOKED = 2
    var selectedIds = ""
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        val views = binding.root
        setContentView(views)
        //setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        //set up back arrow
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.menuLogout -> signOut()
                R.id.menuTime -> Toast.makeText(applicationContext, "Clicked time", Toast.LENGTH_SHORT).show()
                R.id.menuViewBookings -> Toast.makeText(applicationContext, "Clicked view bookings", Toast.LENGTH_SHORT).show()
            }
            true
        }

        layout = binding.layoutTable
        tables = "/$tables"
        val layoutSeat = LinearLayout(this)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutSeat.orientation = LinearLayout.VERTICAL
        layoutSeat.layoutParams = params
        layoutSeat.setPadding(
            8 * tableSpacing,
            8 * tableSpacing,
            8 * tableSpacing,
            8 * tableSpacing
        )
        //below line may break stuff
        layout?.addView(layoutSeat)
        var layout: LinearLayout? = null
        var count = 0
        for (index in 0 until tables.length) {
            if (tables[index] == '/') {
                layout = LinearLayout(this)
                layout.orientation = LinearLayout.HORIZONTAL
                layoutSeat.addView(layout)
            } else if (tables[index] == 'U') {
                count++
                val view = TextView(this)
                val layoutParams = LinearLayout.LayoutParams(tableSize, tableSize)
                layoutParams.setMargins(tableSpacing, tableSpacing, tableSpacing, tableSpacing)
                view.layoutParams = layoutParams
                view.setPadding(0, 0, 0, 2 * tableSpacing)
                view.id = count
                view.gravity = Gravity.CENTER
                view.setBackgroundResource(R.drawable.ic_table_reserved_big)
                view.setTextColor(Color.WHITE)
                view.tag = STATUS_BOOKED
                view.text = count.toString() + ""
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
                layout!!.addView(view)
                tableViewList.add(view)
                view.setOnClickListener(this)
            } else if (tables[index] == 'A') {
                count++
                val view = TextView(this)
                val layoutParams = LinearLayout.LayoutParams(tableSize, tableSize)
                layoutParams.setMargins(tableSpacing, tableSpacing, tableSpacing, tableSpacing)
                view.layoutParams = layoutParams
                view.setPadding(0, 0, 0, 2 * tableSpacing)
                view.id = count
                view.gravity = Gravity.CENTER
                view.setBackgroundResource(R.drawable.ic_table_available)
                view.text = count.toString() + ""
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
                view.setTextColor(Color.BLACK)
                view.tag = STATUS_AVAILABLE
                layout!!.addView(view)
                tableViewList.add(view)
                view.setOnClickListener(this)
            } else if (tables[index] == 'E') {
                val view = TextView(this)
                val layoutParams = LinearLayout.LayoutParams(150, 150)
                layoutParams.setMargins(tableSpacing, tableSpacing, tableSpacing, tableSpacing)
                view.layoutParams = layoutParams
                view.setPadding(0, 0, 0, 2 * tableSpacing)
                view.id = count
                view.gravity = Gravity.CENTER
                view.setBackgroundResource(R.drawable.ic_emergency_exit)
                layout!!.addView(view)
                tableViewList.add(view)
            } else if (tables[index] == 'T') {
                val view = TextView(this)
                val layoutParams = LinearLayout.LayoutParams(150, 150)
                layoutParams.setMargins(tableSpacing, tableSpacing, tableSpacing, tableSpacing)
                view.layoutParams = layoutParams
                view.setPadding(0, 0, 0, 2 * tableSpacing)
                view.id = count
                view.gravity = Gravity.CENTER
                view.setBackgroundResource(R.drawable.ic_tv)
                layout!!.addView(view)
                tableViewList.add(view)
            } else if (tables[index] == 'B') {
                val view = TextView(this)
                val layoutParams = LinearLayout.LayoutParams(150, 150)
                layoutParams.setMargins(tableSpacing, tableSpacing, tableSpacing, tableSpacing)
                view.layoutParams = layoutParams
                view.setPadding(0, 0, 0, 2 * tableSpacing)
                view.id = count
                view.gravity = Gravity.CENTER
                view.setBackgroundResource(R.drawable.ic_bar)
                layout!!.addView(view)
                tableViewList.add(view)
            } else if (tables[index] == 'b') {
                val view = TextView(this)
                val layoutParams = LinearLayout.LayoutParams(150, 150)
                layoutParams.setMargins(tableSpacing, tableSpacing, tableSpacing, tableSpacing)
                view.layoutParams = layoutParams
                view.setPadding(0, 0, 0, 2 * tableSpacing)
                view.id = count
                view.gravity = Gravity.CENTER
                view.setBackgroundResource(R.drawable.ic_bathroom)
                layout!!.addView(view)
                tableViewList.add(view)
            } else if (tables[index] == '_') {
                val view = TextView(this)
                val layoutParams = LinearLayout.LayoutParams(tableSize, tableSize)
                layoutParams.setMargins(tableSpacing, tableSpacing, tableSpacing, tableSpacing)
                view.layoutParams = layoutParams
                view.setBackgroundColor(Color.TRANSPARENT)
                view.text = ""
                layout!!.addView(view)
            }
        }
    }

    override fun onClick(view: View?) {
        if (view?.tag as Int == STATUS_AVAILABLE) {
            if (selectedIds.contains(view.id.toString() + ",")) {
                //selectedIds = selectedIds.replace(+view.id.toString() + ",", "")
                selectedIds = selectedIds.replace(view.id.toString() + ",", "")
                view.setBackgroundResource(R.drawable.ic_table_available)
            } else {
                selectedIds = selectedIds + view.id + ","
                view.setBackgroundResource(R.drawable.ic_table_reserved_big)
            }
        } else if (view.tag as Int == STATUS_BOOKED) {
            Toast.makeText(this, "Seat " + view.id + " is Booked", Toast.LENGTH_SHORT).show()
            /**} else if (view.tag as Int == STATUS_RESERVED) {
            Toast.makeText(this, "Seat " + view.id + " is Reserved", Toast.LENGTH_SHORT).show() **/
        }
    }

    private fun signOut() {
        auth!!.signOut()
        startActivity(Intent(applicationContext, Login::class.java))
        Toast.makeText(applicationContext, "Clicked logout", Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}