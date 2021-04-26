package com.example.seatpickerapp.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seatpickerapp.R
import com.example.seatpickerapp.activities.OrderFoodActivity
import com.example.seatpickerapp.activities.PreOrderMealActivity
import com.example.seatpickerapp.dataClasses.FoodItem
import com.example.seatpickerapp.databinding.FragmentPreOrderItemsBinding
import com.example.seatpickerapp.interfaces.Communicator
import com.example.seatpickerapp.interfaces.PreOrderCommunicator
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import java.util.*


class PreOrderItemsFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    val db = FirebaseFirestore.getInstance()
    private var _binding: FragmentPreOrderItemsBinding?= null
    private val binding get() = _binding!!
    private var adapter: PreOrderItemsFragment.ProductFirestoreRecyclerAdapter? = null
    private lateinit var communicator: PreOrderCommunicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPreOrderItemsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        //displayMessage = arguments?.getString("restaurant")
        Log.d("PreOrderItemsFrag", PreOrderMealActivity.preOrderCategory.toString())

        val itemRef = db.collection("restaurants").document(FlanagansFragment.preOrderRestaurant.replace("\\s".toRegex(), "").decapitalize(Locale.ROOT))
            .collection("inHouseMenu").document(PreOrderMealActivity.preOrderCategory).collection("items")
        binding.preOrderItemsRV.layoutManager = LinearLayoutManager(context)
        val options = FirestoreRecyclerOptions.Builder<FoodItem>().setQuery(itemRef, FoodItem::class.java).build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.preOrderItemsRV.adapter = adapter

        communicator = activity as PreOrderCommunicator

        binding.preOrderItemsBackBtn.setOnClickListener {
            communicator.passDataCommunicator("back")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

        fun setContent(name: String, image: String, price: Double ) {
            val nameTextView = view.findViewById<TextView>(R.id.itemNameTxtView)
            val imageView = view.findViewById<ImageView>(R.id.itemImage)
            val priceTextView = view.findViewById<TextView>(R.id.itemPriceTextView)

            nameTextView.text = name
            val rounded = String.format("%.2f", price)
            priceTextView.text = "€$rounded"

            if (image.isNotEmpty()) {
                Picasso.with(context).load(image).into(imageView)
            }
        }

        fun itemSelected(name: String, image: String, price: Double) {
            val cardViewItem= view.findViewById<CardView>(R.id.cardViewFoodItem)
            cardViewItem.setOnClickListener {

                val dialogView = layoutInflater.inflate(R.layout.dialog_add_to_cart, null)
                val customDialog = AlertDialog.Builder(activity!!)
                    .setView(dialogView)

                Log.d("FoodItemsFragment", "$name ${price.toString()}")

                val alertD: AlertDialog = customDialog.show()

                val nameTextView = dialogView.findViewById<TextView>(R.id.item_name_dialog)
                val priceText = dialogView.findViewById<TextView>(R.id.item_price_dialog)
                nameTextView.text = name
                val rounded = String.format("%.2f", price)
                //priceText.text = "€$rounded"
                priceText.text = ""

                val addToOrderTextView = dialogView.findViewById<TextView>(R.id.addToOrderTxtView)
                addToOrderTextView.text = "Pre order for your table"

                dialogView.findViewById<LinearLayout>(R.id.order_button_container).setOnClickListener {


                    db.collection("users").document(auth?.uid.toString()).collection("booking")
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                Log.d(TAG, "${document.id} => ${document.data}")
                                var date = TableRestaurantFragment.currentDate
                                var time = TableRestaurantFragment.currentTime
                                var tableNo = TableRestaurantFragment.currentTableNo

                                if (document.get("date") == date && document.get("time") == time && document.get("tableNo") == tableNo) {
                                    Log.d("PreOrderItems", document.toString())

                                    val data = hashMapOf(
                                        "name" to name,
                                        "image" to image,
                                        "price" to price
                                     )

                                    db.collection("users").document(auth?.uid.toString()).collection("booking").document(document.id).collection("preOrderItems")
                                        .add(data)
                                        .addOnSuccessListener { documentReference ->
                                            Log.d("PreOrderItems", "DocumentSnapshot written with ID: ${documentReference.id}")
                                            Toast.makeText(context, "$name has been added to your table for $time on $data", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w("PreOrderItems", "Error adding document", e)
                                        }

                                }
                                else {
                                    Log.d("PreOrderItems", "document not found")
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w(TAG, "Error getting documents: ", exception)
                        }




                    alertD.dismiss()
                }
            }
        }

    }

private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<FoodItem>) : FirestoreRecyclerAdapter<FoodItem, PreOrderItemsFragment.ProductViewHolder>(options) {


    override fun onBindViewHolder(productViewHolder: PreOrderItemsFragment.ProductViewHolder, position: Int, foodItem: FoodItem) {
        productViewHolder.setContent(foodItem.name, foodItem.image, foodItem.price)

        productViewHolder.itemSelected(foodItem.name, foodItem.image, foodItem.price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreOrderItemsFragment.ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_item, parent, false)
        return ProductViewHolder(view)
    }
}


}