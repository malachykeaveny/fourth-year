package com.example.seatpickerapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seatpickerapp.R
import com.example.seatpickerapp.dataClasses.FoodItem
import com.example.seatpickerapp.databinding.ActivityEditMenuBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import java.util.*

class EditMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditMenuBinding
    private val db = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth? = null
    private var adapter: EditMenuActivity.ProductFirestoreRecyclerAdapter? = null
    private var spinner: Spinner? = null
    val subjects: MutableList<String?> = ArrayList()
    private var selectedCategory: String? = null
    private var selectedCategoryLong: String? = null
    private var restaurant: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMenuBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        restaurant = intent.getStringExtra("restaurant").toString().decapitalize(Locale.ROOT)
            .replace("\\s".toRegex(), "")
        Log.d("EditMenuActivity", restaurant!!)

        var menuCollectionRef =
            db.collection("restaurants").document(restaurant!!).collection("menu")
                .document(selectedCategory.toString().toLowerCase(Locale.ROOT)).collection("items")
        binding.editMenuRV.layoutManager = LinearLayoutManager(this)
        val options = FirestoreRecyclerOptions.Builder<FoodItem>()
            .setQuery(menuCollectionRef, FoodItem::class.java).build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.editMenuRV.adapter = adapter

        spinner = binding.editMenuSpinner
        spinner()

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
                selectedCategoryLong = subjects[position].toString()
                selectedCategory = selectedCategoryLong.toString().replace("\\s".toRegex(), "")
                setupRecyclerView(selectedCategory.toString())
                Log.d("EditMenuActivity", selectedCategory.toString().toLowerCase(Locale.ROOT))
            }
        }

        binding.addMenuItemButton.setOnClickListener {
            Log.d("EditMenuActivity", "Add: $selectedCategory")
            setupAlertDialog()
        }
    }

    private fun setupAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add item to $selectedCategoryLong")
        val view = LayoutInflater.from(application).inflate(R.layout.dialog_add_food_item, null)
        val name = view.findViewById<EditText>(R.id.addItemNameTxt)
        val price = view.findViewById<EditText>(R.id.addItemPriceTxt)
        val image = view.findViewById<EditText>(R.id.addItemImageTxt)

        builder.setView(view)
        builder.setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton("Done", null)

        val dialog = builder.create()
        dialog.show()

        val confirmButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        confirmButton.setOnClickListener {
            when {
                name.text.isEmpty() -> name.setError("Must not be empty")
                price.text.isEmpty() -> price.setError("Must not be empty")
                !isNumber(price.text.toString()) -> price.setError("Must be a number")
                image.text.isEmpty() -> image.setError("Must not be empty")
                else -> {
                    val addItemData = hashMapOf(
                        "name" to name.text.toString(),
                        "price" to price.text.toString().toDouble(),
                        "image" to image.text.toString()
                    )

                    Log.d("addItemsCheck", addItemData.toString())

                    val addItemCollectionRef =
                        db.collection("restaurants").document(restaurant!!).collection("menu")
                            .document(selectedCategory.toString().toLowerCase(Locale.ROOT)).collection("items")

                    addItemCollectionRef
                        .add(addItemData)
                        .addOnSuccessListener { documentReference ->
                            Log.d(
                                "EditMenuActivity",
                                "DocumentSnapshot written with ID: ${documentReference.id}"
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.w("EditMenuActivity", "Error adding document", e)
                        }
                }
            }
        }
    }

    private fun isNumber(text: String): Boolean {
        var dot = 0
        val integers = '0'..'9'
        return text.all { it in integers || it == '.' && dot++ < 1 }
    }

    private fun setupRecyclerView(selectedCategory: String) {
        adapter?.stopListening()
        val menuCollectionRef =
            db.collection("restaurants").document(restaurant!!).collection("menu").document(
                selectedCategory.toString().toLowerCase(
                    Locale.ROOT
                )
            ).collection("items")
        binding.editMenuRV.layoutManager = LinearLayoutManager(this)
        val options = FirestoreRecyclerOptions.Builder<FoodItem>()
            .setQuery(menuCollectionRef, FoodItem::class.java).build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.editMenuRV.adapter = adapter
        adapter!!.startListening()
    }

    private fun spinner() {
        val staffCollectionRef =
            db.collection("restaurants").document(restaurant!!).collection("menu")
        val spinnerAdapter =
            ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, subjects)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = spinnerAdapter
        staffCollectionRef.get().addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    val subject = document.getString("name")
                    subjects.add(subject)
                }
                spinnerAdapter.notifyDataSetChanged()
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
        fun setContent(name: String, image: String, price: Double) {
            val nameTextView = view.findViewById<TextView>(R.id.itemNameTxtView)
            val imageView = view.findViewById<ImageView>(R.id.itemImage)
            val priceTextView = view.findViewById<TextView>(R.id.itemPriceTextView)

            nameTextView.text = name
            val rounded = String.format("%.2f", price)
            priceTextView.text = "€$rounded"

            if (image.isNotEmpty()) {
                Picasso.with(applicationContext).load(image).into(imageView)
            }
        }

        fun editItem(name: String, price: Double, image: String, id: String) {
            val cardViewItem = view.findViewById<CardView>(R.id.cardViewFoodItem)

            cardViewItem.setOnClickListener {
                val builder = AlertDialog.Builder(
                    this@EditMenuActivity,
                    R.style.Theme_AppCompat_Light_Dialog_Alert
                )
                builder.setTitle("Edit $name")
                val view =
                    LayoutInflater.from(application).inflate(R.layout.dialog_edit_food_item, null)

                val itemName = view.findViewById<EditText>(R.id.editItemNameTxt)
                val itemPrice = view.findViewById<EditText>(R.id.editItemPriceTxt)

                itemName.setText(name)
                itemPrice.setText(price.toString())

                builder.setView(view)

                builder.setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
                    .setPositiveButton("Confirm") { dialogInterface, _ ->
                        val itemDocRef =
                            db.collection("restaurants").document(restaurant!!).collection("menu")
                                .document(
                                    selectedCategory.toString().toLowerCase(
                                        Locale.ROOT
                                    )
                                ).collection("items").document(id)

                        itemDocRef
                            .update(
                                "name", itemName.text.toString(),
                                "price", itemPrice.text.toString().toDouble()
                            )
                            .addOnSuccessListener {
                                Log.d("EditMenuActivity", "DocumentSnapshot successfully updated!")
                                Toast.makeText(
                                    applicationContext,
                                    "$name successfully updated!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Log.w(
                                    "EditMenuActivity",
                                    "Error updating document",
                                    e
                                )
                            }
                    }
                    .setNeutralButton("Delete") { dialogInterface, _ ->
                        val itemDocRef =
                            db.collection("restaurants").document(restaurant!!).collection("menu")
                                .document(
                                    selectedCategory.toString().toLowerCase(
                                        Locale.ROOT
                                    )
                                ).collection("items").document(id)

                        itemDocRef
                            .delete()
                            .addOnSuccessListener {
                                Log.d("EditMenuActivity", "DocumentSnapshot successfully deleted!")
                                Toast.makeText(
                                    applicationContext,
                                    "$name successfully deleted!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Log.w(
                                    "EditMenuActivity",
                                    "Error deleting document",
                                    e
                                )
                            }
                    }

                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<FoodItem>) :
        FirestoreRecyclerAdapter<FoodItem, EditMenuActivity.ProductViewHolder>(
            options
        ) {
        override fun onBindViewHolder(
            productViewHolder: EditMenuActivity.ProductViewHolder,
            position: Int,
            foodItem: FoodItem
        ) {
            productViewHolder.setContent(foodItem.name, foodItem.image, foodItem.price)
            productViewHolder.editItem(
                foodItem.name,
                foodItem.price,
                foodItem.image,
                snapshots.getSnapshot(position).id
            )
            //productViewHolder.deleteItem(snapshots.getSnapshot(position).id)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): EditMenuActivity.ProductViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_food_item, parent, false)
            return ProductViewHolder(view)
        }
    }
}