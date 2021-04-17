package com.example.softwarepatternsca2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.softwarepatternsca2.databinding.ActivityViewCustomerOrdersBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.ArrayList

class ViewCustomerOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewCustomerOrdersBinding
    private var auth: FirebaseAuth? = null
    private var db = FirebaseFirestore.getInstance()
    private var adapter: ViewCustomerOrdersActivity.ProductFirestoreRecyclerAdapter? = null
    val subjects: MutableList<String?> = ArrayList()
    private var spinner: Spinner? = null
    private var selectedUser: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewCustomerOrdersBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()
        spinner = binding.spinner

        val userCollectionRef = db.collection("users").document("w0lkrxgeAGblV2K1fsl6CwJEyhp2").collection("order")
        binding.viewUserOrdersRv.layoutManager = LinearLayoutManager(applicationContext)
        val options = FirestoreRecyclerOptions.Builder<Order>().setQuery(userCollectionRef, Order::class.java).build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.viewUserOrdersRv.adapter = adapter

        setupSpinner()

        spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                //Toast.makeText(applicationContext, "${subjects[position]}", Toast.LENGTH_SHORT).show()
                Log.d("spinner", "${subjects[position]}")
                selectedUser = subjects[position].toString()
                setupRecyclerView()
            }

        }

    }

    private fun setupSpinner() {
        val userCollectionref = db.collection("users")
        val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, subjects)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = adapter
        userCollectionref.get().addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    val subject = document.getString("name")
                    //val docId = document.id
                    subjects.add(subject)
                }
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun setupRecyclerView() {
        var userDocId = ""
        var userCollectionRef = db.collection("users")
        userCollectionRef.get().addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    if (document.getString("name") == selectedUser) {
                        userDocId = document.id
                        Log.d("checkForID", userDocId)

                        Log.d("checkHasIDReached", "$userDocId got this far")
                        adapter?.stopListening()
                        var orderCollectionRef = db.collection("users").document(userDocId).collection("order")
                        binding.viewUserOrdersRv.layoutManager = LinearLayoutManager(applicationContext)
                        val options = FirestoreRecyclerOptions.Builder<Order>().setQuery(orderCollectionRef, Order::class.java).build()
                        adapter = ProductFirestoreRecyclerAdapter(options)
                        binding.viewUserOrdersRv.adapter = adapter
                        adapter!!.startListening()
                    }
                }
            }
        })



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



    private inner class ProductViewHolder internal constructor(private val view: View) :
            RecyclerView.ViewHolder(view) {

        fun setContent(address: String, date: String, items: String, time: String) {
            val itemsTextView = view.findViewById<TextView>(R.id.itemsTextView)
            val dateTextView = view.findViewById<TextView>(R.id.dateTextView)
            val timeTextView = view.findViewById<TextView>(R.id.timeTextView)
            val addressTextView = view.findViewById<TextView>(R.id.addressTextView)

            itemsTextView.text = items
            dateTextView.text = date
            timeTextView.text = time
            addressTextView.text = address

            Log.d("checkingOrder", "$address $date $items $time")
        }

    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Order>) : FirestoreRecyclerAdapter<Order, ProductViewHolder>(options) {

        override fun onBindViewHolder(productViewHolder: ViewCustomerOrdersActivity.ProductViewHolder, position: Int, order: Order) {
            productViewHolder.setContent(order.address, order.date, order.items, order.time)
            //productViewHolder.itemSelected(item.itemName, item.price, item.image, item.stock)
            //productViewHolder.deleteItem(snapshots.getSnapshot(position).id)
        }

        override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
        ): ViewCustomerOrdersActivity.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
            return ProductViewHolder(view)
        }
    }
}