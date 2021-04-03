package com.example.seatpickerapp.fragments

import android.media.Image
import androidx.appcompat.app.AlertDialog
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
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seatpickerapp.R
import com.example.seatpickerapp.activities.OrderFoodActivity
import com.example.seatpickerapp.dataClasses.FoodItem
import com.example.seatpickerapp.databinding.FragmentFoodItemsBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class FoodItemsFragment : Fragment() {

    private var auth: FirebaseAuth? = null
    private var adapter: FoodItemsFragment.ProductFirestoreRecyclerAdapter? = null
    val db = FirebaseFirestore.getInstance()
    private var _binding: FragmentFoodItemsBinding?= null
    private val binding get() = _binding!!
    var displayMessage: String?= ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodItemsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        displayMessage = arguments?.getString("category")
        Log.d("FoodItemsFragment", OrderFoodActivity.currentCategory)
        Log.d("FoodItemsFragment", OrderFoodActivity.currentRestaurant)

        val itemsRef = db.collection("restaurants").document(OrderFoodActivity.currentRestaurant).collection("menu").document(OrderFoodActivity.currentCategory
            .toLowerCase(Locale.getDefault())).collection("items")

        Log.d("FoodItemsFragment", displayMessage.toString().toLowerCase(Locale.getDefault()))

        binding.foodItemRV.layoutManager = LinearLayoutManager(context)
        val options = FirestoreRecyclerOptions.Builder<FoodItem>().setQuery(itemsRef, FoodItem::class.java).build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.foodItemRV.adapter = adapter
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
                priceText.text = "€$rounded"

                dialogView.findViewById<LinearLayout>(R.id.order_button_container).setOnClickListener {
                    //Toast.makeText(context, "$name $rounded", Toast.LENGTH_SHORT).show()
                    CoroutineScope(Dispatchers.IO).launch {
                        val userCartrRef = db.collection("users").document(auth?.uid.toString()).collection("cart")
                        var foundDuplicate = false
                        try {
                            val querySnapshot = userCartrRef.get().await()
                            for (document in querySnapshot.documents) {
                                if (document.get("itemName") == name) {
                                    foundDuplicate = true
                                    val quantity = document.get("quantity").toString().toInt()
                                    userCartrRef.document(document.id).update("quantity", quantity + 1)
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "We've updated the items quantity in the cart!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                            if (!foundDuplicate) {
                                val itemToCart = hashMapOf(
                                    "itemName" to name,
                                    "itemPrice" to price,
                                    "quantity" to 1,
                                    "image" to image
                                )

                                userCartrRef.add(itemToCart)
                                    .addOnSuccessListener { Log.d("FoodItemsFragment", "$name added to cart") }
                                    .addOnFailureListener { e -> Log.w("FoodItemsFragment", "Error updating admin collection with booking", e) }
                            }

                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                //Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                                Log.d("FoodItemsFragment", e.message.toString())
                            }
                        }
                    }

                    alertD.dismiss()
                }
            }
        }

    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<FoodItem>) : FirestoreRecyclerAdapter<FoodItem, FoodItemsFragment.ProductViewHolder>(options) {


        override fun onBindViewHolder(productViewHolder: FoodItemsFragment.ProductViewHolder, position: Int, foodItem: FoodItem) {
            productViewHolder.setContent(foodItem.name, foodItem.image, foodItem.price)

            productViewHolder.itemSelected(foodItem.name, foodItem.image, foodItem.price)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemsFragment.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_item, parent, false)
            return ProductViewHolder(view)
        }
    }


}