package com.example.softwarepatternsca2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.softwarepatternsca2.databinding.ActivityCommentsBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class CommentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommentsBinding
    private var auth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance()
    private var adapter: CommentsActivity.ProductFirestoreRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        val documentId: String = intent.getStringExtra("documentId").toString()
        Log.d("CommentsActivity", documentId)

        val itemCommentCollectionRef = db.collection("items").document(documentId).collection("comments")
        binding.commentsRV.layoutManager = LinearLayoutManager(this)
        val options = FirestoreRecyclerOptions.Builder<Comment>().setQuery(itemCommentCollectionRef, Comment::class.java).build()
        adapter = ProductFirestoreRecyclerAdapter(options)
        binding.commentsRV.adapter = adapter

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

        fun setContent(comment: String) {
            val commentTextView = view.findViewById<TextView>(R.id.commentTextView)

            commentTextView.text = comment

        }

    }

    private inner class ProductFirestoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<Comment>) :
            FirestoreRecyclerAdapter<Comment, CommentsActivity.ProductViewHolder>(options) {
        override fun onBindViewHolder(productViewHolder: CommentsActivity.ProductViewHolder, position: Int, comment: Comment) {
            productViewHolder.setContent(comment.commentText)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsActivity.ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
            return ProductViewHolder(view)
        }
    }

}