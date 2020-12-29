package com.example.kotlinpractice

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinpractice.databinding.ActivityReservationStepThreeBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.item_seat.*

class ReservationStepThree : AppCompatActivity() {

    private lateinit var binding: ActivityReservationStepThreeBinding
    private var adapter: ProductFirestoreRecyclerAdapter? = null
    val db = FirebaseFirestore.getInstance()
    val tableCollectionRef = db.collection("Tables").document("tableEight").collection("29.12.2020")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservationStepThreeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //setContentView(R.layout.activity_reservation_step_three)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        //val query = rootRef!!.collection("Tables").orderBy("seats", Query.Direction.DESCENDING)
        //val query = db.collection("Tables").document("tableSeven").collection("29.12.2020")
        val options = FirestoreRecyclerOptions.Builder<Table>().setQuery(tableCollectionRef, Table::class.java).build()
        //Log.d("TableTesting", query.)

        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()

        if (adapter != null) {
            adapter!!.stopListening()
        }
    }

    private inner class ProductViewHolder internal constructor(private val view: View) : RecyclerView.ViewHolder(view) {
        val db = FirebaseFirestore.getInstance()

        internal fun setProductName(seat: String) {
            val textView = view.findViewById<TextView>(R.id.seat_text_view)
            val card_View = view.findViewById<CardView>(R.id.cardView)

            if (seat == "20.00-22.00") {
                card_View.isClickable = false
                card_View.setBackgroundColor(Color.GRAY)
                textView.setTextColor(Color.WHITE)
                textView.text = seat
            }
            else {
                textView.text = seat
            }
        }
    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Table>) : FirestoreRecyclerAdapter<Table, ProductViewHolder>(options) {
        override fun onBindViewHolder(productViewHolder: ProductViewHolder, position: Int, productModel: Table) {
            productViewHolder.setProductName(productModel.time)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_seat, parent, false)
            return ProductViewHolder(view)
        }
    }

}