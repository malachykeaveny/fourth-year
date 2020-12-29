package com.example.kotlinpractice

import android.content.Intent
import android.gesture.GestureOverlayView
import android.gesture.GestureOverlayView.OnGestureListener
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.animation.ScaleAnimation
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.util.*


class DashboardActivity: AppCompatActivity(), View.OnClickListener {

    private val tableCollectionRef = Firebase.firestore.collection("Tables").document("tableOne").collection(
        "Monday"
    )
    lateinit var toggle: ActionBarDrawerToggle
    var layout: ViewGroup? = null
    var tables = ("E___T___/"
            + "_A__A__A/"
            + "________/"
            + "B_A__U__/"
            + "_________b/"
            + "_A__A__A/"
            )
    var tableViewList: MutableList<TextView> = ArrayList()
    var tableSize = 150
    var tableSpacing = 1
    var STATUS_AVAILABLE = 1
    var STATUS_BOOKED = 2
    var selectedIds = ""
    val view: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        layout = layoutTable
        tables = "/$tables"
        val layoutSeat = LinearLayout(this)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutSeat.orientation = LinearLayout.VERTICAL
        layoutSeat.layoutParams = params
        layoutSeat.setPadding(
            1 * tableSpacing,
            1 * tableSpacing,
            1 * tableSpacing,
            1 * tableSpacing
        )
        //below line may break stuff
        layout?.addView(layoutSeat)
        var layout: LinearLayout? = null
        var count = 0
        for (index in tables.indices) {
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
                val layoutParams = LinearLayout.LayoutParams(100, 100)
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
                val layoutParams = LinearLayout.LayoutParams(100, 100)
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
                val layoutParams = LinearLayout.LayoutParams(100, 100)
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
                val layoutParams = LinearLayout.LayoutParams(100, 100)
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
                val layoutParams = LinearLayout.LayoutParams(85, 85)
                layoutParams.setMargins(tableSpacing, tableSpacing, tableSpacing, tableSpacing)
                view.layoutParams = layoutParams
                view.setBackgroundColor(Color.TRANSPARENT)
                view.text = ""
                layout!!.addView(view)
            }
        }

        changeTableButton.setOnClickListener{
            changeTable(view)
            //val myc = findViewById<TextView>(view.id.)
    }

    }

    private fun changeTable(view: View?) {
        var myView = findViewById<HorizontalScrollView>(R.id.layoutTable)
        layoutTable.getChildAt(0)
            if (view?.id.toString() == "1") {
                view?.setBackgroundResource(R.drawable.ic_table_reserved_big)
            }
            else {
                Toast.makeText(this@DashboardActivity, "View id: " + view?.id.toString(), Toast.LENGTH_SHORT).show()
            }


    }

    override fun onClick(view: View?) {
        Log.d("TableId", view.toString())
        Toast.makeText(this@DashboardActivity, ("table number: " + view?.toString()), Toast.LENGTH_SHORT).show()
        if (view?.tag as Int == STATUS_AVAILABLE) {
            val popupMenu: PopupMenu = PopupMenu(this, view)
            //popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
            //popupMenu.show()
            Log.d("DbAct: Table No: ", view.id.toString())
            //val intent = Intent(this@DashboardActivity, ReservationStepTwo::class.java)
            //ntent.putExtra("tableNumber", view.id.toString())
            //startActivity(intent)
            //Toast.makeText(this@DashboardActivity, ("table number: " + view.id.toString()), Toast.LENGTH_SHORT).show()
        }
    }

    private fun retrieveTables() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapshot = tableCollectionRef.get().await()
            //val querySnapshot = tableCollectionRef.document()
            val sb = StringBuilder()
            for(document in querySnapshot.documents) {
                //val table = document.toObject<Day>()
                val table = document.id + " reserved: " + document.get("reserved")
                sb.append("$table\n")
            }
            withContext(Dispatchers.Main) {
                tvUsers.text = sb.toString()
            }
        }
        catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@DashboardActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}