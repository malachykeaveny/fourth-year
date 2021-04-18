package com.example.softwarepatternsca2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.softwarepatternsca2.databinding.ActivityViewItemsBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.roundToLong

class ViewItemsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewItemsBinding
    private val db = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth? = null
    private var adapter: ViewItemsActivity.ProductFirestoreRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewItemsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        setupRecyclerView()

        binding.rvSearchBtn.setOnClickListener {
           // Log.d("itemsColRef", itemsCollectionRef.whereArrayContains("category", binding.rvSearchEditTxt.text.toString()).toString())
            searchRecyclerView(binding.rvSearchEditTxt.text.toString().toLowerCase(Locale.ROOT).capitalize(Locale.ROOT))
        }

        binding.rvSearchEditTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(searchChars: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchRecyclerView(searchChars.toString().toLowerCase(Locale.ROOT).capitalize(Locale.ROOT))
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun setupRecyclerView() {
        var itemsCollectionRef = db.collection("items")
        //Log.d("itemsColRef", itemsCollectionRef.whereArrayContains("category", "Televisions").toString())
        binding.viewItemsRV.layoutManager = LinearLayoutManager(this)
        val options = FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(itemsCollectionRef, Item::class.java).build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.viewItemsRV.adapter = adapter
    }

    private fun searchRecyclerView(searchText: String) {

        if (searchText.isEmpty()) {
            Toast.makeText(this, "EMPTY", Toast.LENGTH_SHORT).show()
            setupRecyclerView()
        }

        adapter?.stopListening()
        var searchCollectionRef = db.collection("items").whereEqualTo("category", searchText)
        var searchCollectionRef2 = db.collection("items").whereGreaterThanOrEqualTo("category", searchText)
        var searchCollectionRef3 = db.collection("items")

        searchCollectionRef2.get()

        binding.viewItemsRV.layoutManager = LinearLayoutManager(applicationContext)
        val options = FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(searchCollectionRef2, Item::class.java).build()

        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.viewItemsRV.adapter = adapter
        adapter!!.startListening()
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
        fun setContent(itemName: String, category: String, manufacturer: String, price: Double, image: String, stock: Int, reviewsTotalSum: Int, noOfReviews: Int) {
            val categoryTextView = view.findViewById<TextView>(R.id.itemCategoryTxtView)
            val manufacturerTextView = view.findViewById<TextView>(R.id.itemManufacturerTxtView)
            val nameTextView = view.findViewById<TextView>(R.id.itemNameTxtView)
            val imageView = view.findViewById<ImageView>(R.id.itemImage)
            val priceTextView = view.findViewById<TextView>(R.id.itemPriceTextView)
            val stockTextView = view.findViewById<TextView>(R.id.itemStockTxtView)
            val reviewTextView = view.findViewById<TextView>(R.id.itemRatingTxtView)

            categoryTextView.text = category
            manufacturerTextView.text = manufacturer
            nameTextView.text = itemName
            priceTextView.text = "€${price.toString()}"
            stockTextView.text = "${stock.toString()} in stock"

            if (image.isNotEmpty()) {
                Picasso.with(applicationContext).load(image).into(imageView)
            }

            var reviewScore = reviewsTotalSum.toDouble() / noOfReviews
            reviewTextView.text = "${reviewScore.roundToLong()}/5 rating($noOfReviews votes)"
        }

        fun itemSelected(itemName: String, price: Double, image: String, stock: Int) {
            val cardViewItem= view.findViewById<CardView>(R.id.cardViewItem)
            cardViewItem.setOnClickListener {

                val dialogView = layoutInflater.inflate(R.layout.dialog_add_to_cart, null)
                val stockEditText = dialogView.findViewById<EditText>(R.id.adminUpdateStockEditText)
                val stockContainer = dialogView.findViewById<LinearLayout>(R.id.adminUpdateStockContainer)
                val customDialog = AlertDialog.Builder(this@ViewItemsActivity, R.style.Theme_AppCompat_Light_Dialog_Alert)
                    .setView(dialogView)

                Log.d("FoodItemsFragment", "$itemName ${price.toString()}")

                val alertD: AlertDialog = customDialog.show()

                val nameTextView = dialogView.findViewById<TextView>(R.id.item_name_dialog)
                val priceText = dialogView.findViewById<TextView>(R.id.item_price_dialog)
                nameTextView.text = itemName
                val rounded = String.format("%.2f", price)
                priceText.text = "€$rounded"

                val userRef = db.collection("users").document(auth?.uid.toString())
                userRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            Log.d("ViewItemsActivity", "DocumentSnapshot data: ${document.data}")
                            if (document.get("hasAdminPrivileges") == true) {
                                stockContainer.visibility = View.VISIBLE
                                stockEditText.setText(stock.toString())
                            }
                            else {
                                stockContainer.visibility = View.INVISIBLE
                            }
                        } else {
                            Log.d("ViewItemsActivity", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("ViewItemsActivity", "get failed with ", exception)
                    }

                dialogView.findViewById<Button>(R.id.updateStockBtn).setOnClickListener {
                    updateStock(itemName, stockEditText.text.toString().toInt())
                    alertD.dismiss()
                }


                dialogView.findViewById<LinearLayout>(R.id.order_button_container).setOnClickListener {
                    //Toast.makeText(context, "$name $rounded", Toast.LENGTH_SHORT).show()
                    CoroutineScope(Dispatchers.IO).launch {
                        val userCartrRef = db.collection("users").document(auth?.uid.toString()).collection("cart")
                        var foundDuplicate = false
                        try {
                            val querySnapshot = userCartrRef.get().await()
                            for (document in querySnapshot.documents) {
                                if (document.get("itemName") == itemName) {
                                    foundDuplicate = true
                                    val quantity = document.get("quantity").toString().toInt()
                                    userCartrRef.document(document.id).update("quantity", quantity + 1)
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(applicationContext, "We've updated the items quantity in the cart!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                            if (!foundDuplicate) {
                                val itemToCart = hashMapOf(
                                    "itemName" to itemName,
                                    "itemPrice" to price,
                                    "quantity" to 1,
                                    "image" to image
                                )

                                userCartrRef.add(itemToCart)
                                    .addOnSuccessListener { Log.d("FoodItemsFragment", "$itemName added to cart") }
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

        private fun updateStock(itemName: String, stock: Int) {
            CoroutineScope(Dispatchers.IO).launch {
                Log.d("CartActivityStock", stock.toString())

                val stockRef = db.collection("items")
                val querySnapshot = stockRef.get().await()
                for (document in querySnapshot.documents) {
                    if (document.get("itemName") == itemName) {
                        //var stock: Int = document.get("stock").toString().toInt()
                        Log.d("ViewItemsStockLoop", itemName)
                        stockRef.document(document.id).
                        update("stock", stock)
                            .addOnSuccessListener {
                                Toast.makeText(applicationContext, "$itemName stock updated!", Toast.LENGTH_SHORT).show()
                                Log.d("ViewItemsActivity", "DocumentSnapshot successfully updated!")
                            }
                            .addOnFailureListener { e -> Log.w("ViewItemsActivity", "Error updating document", e) }
                    }
                    else {
                        Log.d("ViewItemsStockLoop", "didn't find $itemName")
                    }
                }

            }
        }

        fun addRating(documentId: String, reviewsTotalSum: Int, noOfReviews: Int) {
            val addRatingBtn = view.findViewById<FloatingActionButton>(R.id.reviewBtn)
            val ratingEditTxt = EditText(this@ViewItemsActivity)

            addRatingBtn.setOnClickListener {
                AlertDialog.Builder(this@ViewItemsActivity, R.style.Theme_AppCompat_Light_Dialog_Alert)
                        .setTitle("Give rating (1-5)")
                        .setView(ratingEditTxt)
                        .setNegativeButton("Cancel") { dialogInterface, i ->
                            val parent = ratingEditTxt.parent as ViewGroup
                            parent.removeAllViews()
                            dialogInterface.dismiss()
                        }
                        .setPositiveButton("OK") { dialogInterface, i ->
                            Log.d("ratingsText", ratingEditTxt.text.toString())
                            var itemDocRef = db.collection("items").document(documentId)
                            itemDocRef
                                    .update("reviewsTotalSum", reviewsTotalSum + ratingEditTxt.text.toString().toInt(), "noOfReviews", noOfReviews + 1 )
                                    .addOnSuccessListener { Log.d("ViewItemsActivity", "DocumentSnapshot successfully updated!") }
                                    .addOnFailureListener { e -> Log.w("ViewItemsActivity", "Error updating document", e) }
                            val parent = ratingEditTxt.parent as ViewGroup
                            parent.removeAllViews()
                        }
                        .show()
            }
        }

        fun addComment(documentId: String) {
            val addCommentBtn = view.findViewById<FloatingActionButton>(R.id.commentBtn)
            val commentEditTxt = EditText(this@ViewItemsActivity)

            addCommentBtn.setOnClickListener {
                AlertDialog.Builder(this@ViewItemsActivity, R.style.Theme_AppCompat_Light_Dialog_Alert)
                        .setTitle("Leave a comment/review you have on this item")
                        .setView(commentEditTxt)
                        .setNegativeButton("Cancel") { dialogInterface, i ->
                            val parent = commentEditTxt.parent as ViewGroup
                            parent.removeAllViews()
                            dialogInterface.dismiss()
                        }
                        .setPositiveButton("OK") { dialogInterface, i ->

                            val comment = hashMapOf(
                                    "commentText" to commentEditTxt.text.toString()
                            )

                            Log.d("commentText", commentEditTxt.text.toString())
                            var itemCommentRef = db.collection("items").document(documentId).collection("comments")
                            itemCommentRef
                                    .add(comment)
                                    .addOnSuccessListener { documentReference -> Log.d("addComment", "DocumentSnapshot written with ID: ${documentReference.id}") }
                                    .addOnFailureListener { e -> Log.w("addComment", "Error adding document", e) }

                            val parent = commentEditTxt.parent as ViewGroup
                            parent.removeAllViews()
                        }
                        .show()
            }
        }

    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Item>) : FirestoreRecyclerAdapter<Item, ProductViewHolder>(options) {

        override fun onBindViewHolder(productViewHolder: ViewItemsActivity.ProductViewHolder, position: Int, item: Item) {
            productViewHolder.setContent(item.itemName, item.category, item.manufacturer, item.price, item.image, item.stock, item.reviewsTotalSum, item.noOfReviews)
            productViewHolder.itemSelected(item.itemName, item.price, item.image, item.stock)
            productViewHolder.addRating(snapshots.getSnapshot(position).id, item.reviewsTotalSum, item.noOfReviews)
            productViewHolder.addComment(snapshots.getSnapshot(position).id)
            //productViewHolder.deleteItem(snapshots.getSnapshot(position).id)
        }

        override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
        ): ViewItemsActivity.ProductViewHolder {
            val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_stock_item, parent, false)
            return ProductViewHolder(view)
        }
    }

}