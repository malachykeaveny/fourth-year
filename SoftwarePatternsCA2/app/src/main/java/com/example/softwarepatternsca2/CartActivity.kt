package com.example.softwarepatternsca2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.softwarepatternsca2.databinding.ActivityCartBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.api.Distribution
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private var auth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance()
    private var adapter: CartActivity.ProductFirestoreRecyclerAdapter? = null
    private var cartTotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        cartTotals()

        val userCartRef = db.collection("users").document(auth?.uid.toString()).collection("cart")
        binding.cartRV.layoutManager = LinearLayoutManager(this)
        val options = FirestoreRecyclerOptions.Builder<CartItem>().setQuery(userCartRef, CartItem::class.java).build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.cartRV.adapter = adapter
    }

    private fun cartTotals() {
        val userCartRef =
            db.collection("users").document(auth?.uid.toString()).collection("cart")
        userCartRef.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            querySnapshot?.let {

                var total = 0.0
                var cartItems = 0

                for (document in it) {
                    if (document.get("quantity").toString().toInt() > 1) {
                        total += document["itemPrice"].toString()
                            .toDouble() * document.get("quantity").toString().toInt()
                        cartItems += document.get("quantity").toString().toInt()
                    } else {
                        total += document["itemPrice"].toString().toDouble()
                        cartItems += 1
                        Log.d("OrderFoodActivities", document["itemPrice"].toString())
                    }
                }

                var roundedItemsAmount = String.format("%.2f", total)
                binding.cartTotalAmount.text = "€$roundedItemsAmount"

                var deliveryAmount = 2.50
                var roundedDeliveryAmount = String.format("%.2f", deliveryAmount)
                binding.deliveryAmount.text = "€$roundedDeliveryAmount"

                var orderTotal = total + deliveryAmount
                val roundedTotal = String.format("%.2f", orderTotal)
                binding.totalPriceAmount.text = "€$roundedTotal"
                cartTotal = roundedTotal.toDouble() * 100

                Log.d("cartTotalCHECKING11", cartTotal.toInt().toString())
            }
        }
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
        RecyclerView.ViewHolder(
            view
        ) {

        fun setContent(name: String, price: Double, img: String) {
            val nameTextView = view.findViewById<TextView>(R.id.cartItemNameTxtView2)
            val priceTextView = view.findViewById<TextView>(R.id.cartItemPriceTxtView2)
            val imageView = view.findViewById<ImageView>(R.id.cartItemImage2)

            nameTextView.text = name
            val rounded = String.format("%.2f", price)
            priceTextView.text = "€$rounded"

            if (img.isNotEmpty()) {
                Picasso.with(applicationContext).load(img).into(imageView)
            }
        }

        fun setupQuantityButton(quantity: Int, documentId: String) {
            val numberButton = view.findViewById<ElegantNumberButton>(R.id.number_button)
            numberButton.setNumber(quantity.toString())

            numberButton.setOnClickListener(ElegantNumberButton.OnClickListener {
                val num: String = numberButton.getNumber()
                db.collection("users").document(auth?.uid.toString()).collection("cart")
                    .document(documentId).update("quantity", num.toString().toInt())
                Log.d("ViewCartActivity", num)
            })
        }

        fun deleteItem(documentId: String) {
            val deleteButton = view.findViewById<ImageView>(R.id.deleteCartItemButton2)

            deleteButton.setOnClickListener {
                db.collection("users").document(auth?.uid.toString()).collection("cart")
                    .document(documentId).delete()
                    .addOnSuccessListener { Log.d("ViewCartActivity", "User DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> Log.w("ViewCartActivity", "Error deleting document", e) }
            }
        }

    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<CartItem>) :
        FirestoreRecyclerAdapter<CartItem, CartActivity.ProductViewHolder>(
            options
        ) {
        override fun onBindViewHolder(
            productViewHolder: CartActivity.ProductViewHolder,
            position: Int,
            cartItem: CartItem
        ) {
            productViewHolder.setContent(cartItem.itemName, cartItem.itemPrice, cartItem.image)
            productViewHolder.setupQuantityButton(
                cartItem.quantity,
                snapshots.getSnapshot(position).id
            )
            productViewHolder.deleteItem(snapshots.getSnapshot(position).id)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): CartActivity.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_cart,
                parent,
                false
            )
            return ProductViewHolder(view)
        }
    }

}