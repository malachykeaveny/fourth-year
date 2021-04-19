package com.example.seatpickerapp.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.seatpickerapp.R
import com.example.seatpickerapp.activities.OrderFoodActivity.Companion.currentRestaurant
import com.example.seatpickerapp.dataClasses.CartItem
import com.example.seatpickerapp.databinding.ActivityViewCartBinding
import com.example.seatpickerapp.stripe.FirebaseEphemeralKeyProvider
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.stripe.android.*
import com.stripe.android.model.Address
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.ShippingInformation
import com.stripe.android.view.BillingAddressFields
import com.stripe.android.view.ShippingInfoWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.StringBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


    class ViewCartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewCartBinding
    private var auth: FirebaseAuth? = null
    val db = FirebaseFirestore.getInstance()
    private var adapter: ViewCartActivity.ProductFirestoreRecyclerAdapter? = null
    private lateinit var paymentSession: PaymentSession
    //private var paymentSession: PaymentSession?= null
    private val stripe: Stripe by lazy { Stripe(applicationContext, "pk_test_51IbvMoJlkOTSiWNOr3RM4HFPbNUF2g8AEbJwbhn8ubivrMpsZffRbkTsv8kDhK01V5YaReWvcT8nuWN5gzQu5ssR00GKQ5T4B3") }
    private lateinit var selectedPaymentMethod: PaymentMethod
    private var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var name: String = ""
    private var phoneNo: String = ""
    private var addressLine1: String = ""
    private var eirCode: String = ""
    private var county: String = ""
    private var cartTotal: Double = 0.0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewCartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()
        PaymentConfiguration.init(applicationContext, "pk_test_51IbvMoJlkOTSiWNOr3RM4HFPbNUF2g8AEbJwbhn8ubivrMpsZffRbkTsv8kDhK01V5YaReWvcT8nuWN5gzQu5ssR00GKQ5T4B3")

        //setUpPaymentSession()
        //binding.goToPaymentButton.isEnabled = false

        val userCartRef = db.collection("users").document(auth?.uid.toString()).collection("cart")
        binding.cartItemsRV.layoutManager = LinearLayoutManager(this)
        val options = FirestoreRecyclerOptions.Builder<CartItem>().setQuery(
            userCartRef,
            CartItem::class.java
        ).build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.cartItemsRV.adapter = adapter

        cartTotals()
        getUserInformation()

        binding.setupPaymentButton.setOnClickListener {
            //setupAlertDialog()
            paymentSession.presentPaymentMethodSelection()
            paymentSession.presentShippingFlow()
        }

        binding.goToPaymentButton.setOnClickListener {
            confirmPayment(selectedPaymentMethod.id!!)
            Log.d("payButton", "YUP")
        }
    }

    private fun getUserInformation() {
        val docRef = db.collection("users").document(auth?.uid.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    name = document.get("name").toString()
                    phoneNo = document.get("phoneNo").toString()
                    addressLine1 = document.get("addressLine1").toString()
                    county = document.get("addressCounty").toString()
                    eirCode = document.get("addressEircode").toString()
                    Log.d("ViewCartActivity", "DocumentSnapshot data: ${document.data}")
                    setUpPaymentSession()
                } else {
                    Log.d("ViewCartActivity", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ViewCartActivity", "get failed with ", exception)
            }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Complete order")
        val view = LayoutInflater.from(application).inflate(R.layout.dialog_place_order, null)
        val addressLine1 = view.findViewById<EditText>(R.id.addressLine1)
        val addressLine2 = view.findViewById<EditText>(R.id.addressLine2)
        val addressLine3 = view.findViewById<EditText>(R.id.addressLine3)
        val eircode = view.findViewById<EditText>(R.id.eirCode)
        val cardPayment = view.findViewById<RadioButton>(R.id.cardPaymentRB)
        val cashOnDelivery = view.findViewById<RadioButton>(R.id.cashPaymentRB)

        builder.setView(view)
        builder.setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton("Done") { dialogInterface, _ ->
            if (cardPayment.isChecked) {
                //completePayment(addressLine1.text.toString(), addressLine2.text.toString(), addressLine3.text.toString(), eircode.text.toString())
            }}

        val dialog = builder.create()
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun completePayment(addressLine1: String, addressLine2: String, addressLine3: String, eircode: String) {
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
                    "restaurant" to OrderFoodActivity.currentRestaurant,
                    "address" to "$addressLine1, $addressLine2, $addressLine3, $eircode",
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
                            Log.d("FoodItemsFragment", "Admin updated with booking!")
                            val querySnapshot1 = userCartRef.get().await()
                            for (document in querySnapshot1.documents) {
                                userCartRef.document(document.id).delete()
                                    .addOnSuccessListener { Log.d("ViewCartActivity", "User DocumentSnapshot successfully deleted!") }
                                    .addOnFailureListener { e -> Log.w("ViewCartActivity", "Error deleting document", e) }
                            }
                        }
                    }
                    .addOnFailureListener { e -> Log.w("ViewCartActivity", "Error updating admin collection with booking", e) }

                Log.d("ViewCartActivity", formatted)

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    //Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    Log.d("FoodItemsFragment", e.message.toString())
                }
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

    private fun setUpPaymentSession() {

        Log.d("setupPaymentCheckingAdd", "$addressLine1, $county, $name")

        CustomerSession.initCustomerSession(this, FirebaseEphemeralKeyProvider())
        paymentSession = PaymentSession(this, PaymentSessionConfig.Builder()
            .setHiddenShippingInfoFields(
                ShippingInfoWidget.CustomizableShippingField.STATE_FIELD
            )
            .setAllowedShippingCountryCodes(setOf("IE"))
            .setPrepopulatedShippingInfo(
                ShippingInformation(
                Address.Builder()
                    .setLine1(addressLine1)
                    .setCity(county)
                    .setPostalCode(eirCode)
                    .setCountry("IE")
                    .build(),
                name,
                phoneNo
            )
            )
            .setShippingInfoRequired(true)
            .setShippingMethodsRequired(false)
            .setBillingAddressFields(BillingAddressFields.None)
            .setShouldShowGooglePay(true)
            .build())

        paymentSession.init(
            object: PaymentSession.PaymentSessionListener {
                override fun onPaymentSessionDataChanged(data: PaymentSessionData) {
                    Log.d("PaymentSession", "PaymentSession has changed: $data")
                    Log.d("PaymentSession", "${data.isPaymentReadyToCharge} <> ${data.paymentMethod}")

                    if (data.isPaymentReadyToCharge) {
                        Log.d("PaymentSession", "Ready to charge");
                        binding.goToPaymentButton.isEnabled = true

                        data.paymentMethod?.let {
                            Log.d("PaymentSession", "PaymentMethod $it selected")
                            binding.setupPaymentButton.text = "${it.card?.brand} card ends with ${it.card?.last4}"
                            selectedPaymentMethod = it
                            binding.goToPaymentButton.isEnabled = true
                        }
                    }
                }

                override fun onCommunicatingStateChanged(isCommunicating: Boolean) {
                    Log.d("PaymentSession",  "isCommunicating $isCommunicating")
                }

                override fun onError(errorCode: Int, errorMessage: String) {
                    Log.e("PaymentSession",  "onError: $errorCode, $errorMessage")
                }
            }
        )
    }

    private fun confirmPayment(paymentMethodId: String) {
        binding.goToPaymentButton.isEnabled = false

        val paymentCollection = Firebase.firestore
            .collection("stripe_customers").document(currentUser?.uid?:"")
            .collection("payments")

        // Add a new document with a generated ID


        Log.d("cartTotalCHECKING", cartTotal.toInt().toString())

        paymentCollection.add(hashMapOf(
            "amount" to cartTotal.toInt(),
            "currency" to "eur"
        ))
            .addOnSuccessListener { documentReference ->
                Log.d("payment", "DocumentSnapshot added with ID: ${documentReference.id}")
                documentReference.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("payment", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d("payment", "Current data: ${snapshot.data}")
                        val clientSecret = snapshot.data?.get("client_secret")
                        Log.d("payment", "Create paymentIntent returns $clientSecret")
                        clientSecret?.let {
                            stripe.confirmPayment(this, ConfirmPaymentIntentParams.createWithPaymentMethodId(
                                paymentMethodId,
                                (it as String)
                            ))

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                createOrder()
                            }
                            //checkoutSummary.text = "Thank you for your payment"
                            Toast.makeText(applicationContext, "Payment Done!!", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Log.e("payment", "Current payment intent : null")
                        binding.goToPaymentButton.isEnabled = true
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("payment", "Error adding document", e)
                binding.goToPaymentButton.isEnabled = true
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOrder() {
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
                    "restaurant" to currentRestaurant,
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
                            Log.d("FoodItemsFragment", "Admin updated with booking!")
                            val querySnapshot1 = userCartRef.get().await()
                            for (document in querySnapshot1.documents) {
                                userCartRef.document(document.id).delete()
                                    .addOnSuccessListener { Log.d("ViewCartActivity", "User DocumentSnapshot successfully deleted!") }
                                    .addOnFailureListener { e -> Log.w("ViewCartActivity", "Error deleting document", e) }
                            }
                        }
                    }
                    .addOnFailureListener { e -> Log.w("ViewCartActivity", "Error updating admin collection with booking", e) }

                startActivity(Intent(applicationContext, HomePageActivity::class.java))
                Log.d("ViewCartActivity", formatted)

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    //Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    Log.d("FoodItemsFragment", e.message.toString())
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        paymentSession.handlePaymentData(requestCode, resultCode, data ?: Intent())
    }

    private inner class ProductViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(
            view
        ) {

        fun setContent(name: String, price: Double, img: String) {
            val nameTextView = view.findViewById<TextView>(R.id.cartItemNameTxtView)
            val priceTextView = view.findViewById<TextView>(R.id.cartItemPriceTxtView)
            val imageView = view.findViewById<ImageView>(R.id.cartItemImage)

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
            val deleteButton = view.findViewById<ImageView>(R.id.deleteCartItemButton)

            deleteButton.setOnClickListener {
                db.collection("users").document(auth?.uid.toString()).collection("cart")
                    .document(documentId).delete()
                    .addOnSuccessListener { Log.d("ViewCartActivity", "User DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> Log.w("ViewCartActivity", "Error deleting document", e) }
            }
        }

    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<CartItem>) :
        FirestoreRecyclerAdapter<CartItem, ViewCartActivity.ProductViewHolder>(
            options
        ) {
        override fun onBindViewHolder(
            productViewHolder: ViewCartActivity.ProductViewHolder,
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
        ): ViewCartActivity.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_cart,
                parent,
                false
            )
            return ProductViewHolder(view)
        }
    }
}