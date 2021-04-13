package com.example.softwarepatternsca2

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.softwarepatternsca2.databinding.ActivityCartBinding
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
import java.lang.StringBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private var auth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance()
    private var adapter: CartActivity.ProductFirestoreRecyclerAdapter? = null
    private var cartTotal: Double = 0.0
    private var addressLine1: EditText?= null
    private var addressLine2: EditText?= null
    private var addressLine3: EditText?= null
    private var eircode: EditText?= null


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

        binding.goToPaymentButton.setOnClickListener {
            setupAlertDialog()
        }
    }

    private fun setupAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add address")
        val view = LayoutInflater.from(application).inflate(R.layout.dialog_place_order, null)
        addressLine1 = view.findViewById<EditText>(R.id.addressLine1)
        addressLine2 = view.findViewById<EditText>(R.id.addressLine2)
        addressLine3 = view.findViewById<EditText>(R.id.addressLine3)
        eircode = view.findViewById<EditText>(R.id.eirCode)
        var cardPayment = view.findViewById<RadioButton>(R.id.cardPaymentRB)

        builder.setView(view)
        builder.setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton("Done") { dialogInterface, _ ->
                if (cardPayment.isChecked) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        //completePayment(addressLine1.text.toString(), addressLine2.text.toString(), addressLine3.text.toString(), eircode.text.toString())
                        cardPayment()
                    }
                }}

        val dialog = builder.create()
        dialog.show()
    }

    private fun cardPayment() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add address")
        val view = LayoutInflater.from(application).inflate(R.layout.dialog_card_payment, null)
        val cardNumber = view.findViewById<EditText>(R.id.cardNumberEditText)
        val cardHolder = view.findViewById<EditText>(R.id.cardHolderNameEditText)
        val cardExpiryDate = view.findViewById<EditText>(R.id.expiryDateEditText)
        val cardCVV = view.findViewById<EditText>(R.id.cvvEditText)

        builder.setView(view)
        builder.setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton("Done") { dialogInterface, _ ->
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    completePayment()
                }

            }

        val dialog = builder.create()
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun completePayment() {
        CoroutineScope(Dispatchers.IO).launch {
            val userCartRef = db.collection("users").document(auth?.uid.toString()).collection("cart")
            val userOrderRef = db.collection("users").document(auth?.uid.toString()).collection("order")
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
            val formatted = current.format(formatter)
            val date = formatted.split(" ")[0]
            val time = formatted.split(" ")[1]

            try {
                val querySnapshot = userCartRef.get().await()
                val builder = StringBuilder()
                for (document in querySnapshot.documents) {
                    var name = document.get("itemName")
                    builder.append(name)
                        .append(",")
                }

                val order = hashMapOf(
                    "address" to "${addressLine1?.text}, ${addressLine2?.text}, ${addressLine3?.text}, ${eircode ?.text}",
                    "items" to builder.toString(),
                    "date" to date,
                    "time" to time
                )

                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, order.toString(), Toast.LENGTH_SHORT).show()
                }

                userOrderRef.add(order)
                    .addOnSuccessListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            Log.d("CartActivity", "Admin updated with booking!")
                            val querySnapshot1 = userCartRef.get().await()
                            for (document in querySnapshot1.documents) {

                                updateStock(document.get("itemName").toString())

                                userCartRef.document(document.id).delete()
                                    .addOnSuccessListener { Log.d("CartActivity", "User DocumentSnapshot successfully deleted!") }
                                    .addOnFailureListener { e -> Log.w("CartActivity", "Error deleting document", e) }
                            }
                        }
                    }
                    .addOnFailureListener { e -> Log.w("CartActivity", "Error updating admin collection with booking", e) }

                Log.d("ViewCartActivity", formatted)

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    //Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    Log.d("FoodItemsFragment", e.message.toString())
                }
            }
        }
    }

    private fun updateStock(itemName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("CartActivityStock", itemName)

            val stockRef = db.collection("items")
            val querySnapshot = stockRef.get().await()
            for (document in querySnapshot.documents) {
                if (document.get("itemName") == itemName) {
                    var stock: Int = document.get("stock").toString().toInt()
                    Log.d("CartActivityStockLoop", itemName)
                    stockRef.document(document.id).
                    update("stock", stock -1)
                        .addOnSuccessListener { Log.d("CartActivity", "DocumentSnapshot successfully updated!") }
                        .addOnFailureListener { e -> Log.w("CartActivity", "Error updating document", e) }
                }
                else {
                    Log.d("CartActivityStockLoop", "didn't find $itemName")
                }
            }

        }

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