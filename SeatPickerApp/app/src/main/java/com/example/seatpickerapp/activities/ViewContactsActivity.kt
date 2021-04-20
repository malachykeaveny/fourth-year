package com.example.seatpickerapp.activities

import android.content.Intent
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
import com.example.seatpickerapp.R
import com.example.seatpickerapp.dataClasses.UserContact
import com.example.seatpickerapp.databinding.ActivityViewContactsBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewContactsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewContactsBinding
    private var auth: FirebaseAuth? = null
    val db = FirebaseFirestore.getInstance()
    private var adapter: ViewContactsActivity.ProductFirestoreRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewContactsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        val userContactRef = db.collection("users").document(auth?.uid.toString()).collection("contacts")
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        val options = FirestoreRecyclerOptions.Builder<UserContact>().setQuery(userContactRef, UserContact::class.java).build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.contactRecyclerView.adapter = adapter
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

        fun setContent(name: String, userId: String) {
            var nameTextView = view.findViewById<TextView>(R.id.contactTextView)

            nameTextView.text = name

            Log.d("checkingOrder", "$name $userId")
        }

        fun itemSelected(name: String, userId: String) {
            var contactCardView = view.findViewById<CardView>(R.id.contactCardView)

            contactCardView.setOnClickListener {

                val userDocRef = db.collection("users").document(auth?.uid.toString())
                userDocRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {

                            val contactCollectionRef = db.collection("users").document(auth?.uid.toString()).collection("contacts")
                            contactCollectionRef.get().addOnSuccessListener { result ->
                                for (targetDocument in result) {
                                    Log.d("contactCollectionRef", "${targetDocument.id} => ${targetDocument.data}")
                                    if (targetDocument.id == userId) {
                                        var targetUserObject = targetDocument.toObject(UserContact::class.java)
                                        Log.d("contactCollectionRef", "$targetUserObject")

                                        var fromUserObject = document.toObject(UserContact::class.java)
                                        Log.d("userDocRef", "DocumentSnapshot data: ${document.data}")

                                        val intent = Intent(this@ViewContactsActivity, MessageActivity::class.java)
                                        intent.putExtra("fromUser", fromUserObject)
                                        intent.putExtra("targetUser", targetUserObject)
                                        intent.putExtra("roomId", "")
                                        Log.d("checkIntent", "fromUser: $fromUserObject , $userId")
                                        startActivity(intent)

                                    }
                                }
                            }
                                .addOnFailureListener { exception ->
                                    Log.d("contactCollectionRef", "Error getting documents: ", exception)
                                }

                            var fromUser = document.toObject(UserContact::class.java)
                            Log.d("userDocRef", "DocumentSnapshot data: ${document.data}")

                            val intent = Intent(this@ViewContactsActivity, MessageActivity::class.java)
                            intent.putExtra("fromUser", fromUser)
                            intent.putExtra("targetUserId", userId)
                            intent.putExtra("roomId", "")
                            Log.d("checkIntent", "fromUser: $fromUser , $userId")
                            startActivity(intent)
                        }
                        else { Log.d("userDocRef", "No such document") }
                    }
                    .addOnFailureListener { exception -> Log.d("userDocRef", "get failed with ", exception) }
            }
        }

    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<UserContact>) : FirestoreRecyclerAdapter<UserContact, ProductViewHolder>(options) {

        override fun onBindViewHolder(productViewHolder: ViewContactsActivity.ProductViewHolder, position: Int, userContact: UserContact) {
            productViewHolder.setContent(userContact.name, userContact.userId)
            productViewHolder.itemSelected(userContact.name, userContact.userId)
            //productViewHolder.deleteItem(snapshots.getSnapshot(position).id)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewContactsActivity.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
            return ProductViewHolder(view)
        }
    }

}