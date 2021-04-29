package com.example.seatpickerapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seatpickerapp.R
import com.example.seatpickerapp.dataClasses.ChatMessage
import com.example.seatpickerapp.dataClasses.UserContact
import com.example.seatpickerapp.databinding.ActivityMessageBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions

class MessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding
    private var auth: FirebaseAuth? = null
    val db = FirebaseFirestore.getInstance()
    private var adapter: ChatMessageAdapter?= null
    private var fromUserObject: UserContact?= null
    private var targetUserObject: UserContact?= null
    private var roomId: String = ""
    private var fromUserRooms: MutableMap<String, Any>?= null
    private var targetUserRooms: MutableMap<String, Any>?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()

        getIntentInfo()

        binding.sendMessageBtn.setOnClickListener {
            sendMessage()
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val messageQuery = db.collection("messages").document(roomId!!).collection("roomMessages").orderBy("timeSentAt", Query.Direction.ASCENDING)
        binding.messagesRV.layoutManager = LinearLayoutManager(applicationContext)
        val options = FirestoreRecyclerOptions.Builder<ChatMessage>().setQuery(messageQuery, ChatMessage::class.java).build()
        adapter = ChatMessageAdapter(options)
        binding.messagesRV.adapter = adapter
    }

    private fun getIntentInfo() {
        fromUserObject = intent.extras?.get("fromUser") as? UserContact
        targetUserObject = intent.extras?.get("targetUser") as? UserContact
        roomId = intent.extras?.get("roomId") as String
        fromUserRooms = fromUserObject?.rooms
        targetUserRooms = targetUserObject?.rooms

        Log.d("MessageActivity", "$fromUserObject $targetUserObject $roomId")

        if (roomId == "") {
            roomId = db.collection("messages").document().id
            if (fromUserRooms != null) {
                for ((key, _) in fromUserRooms!!) {
                    if (targetUserRooms != null) {
                        if (targetUserRooms!!.contains(key)) {
                            roomId = key
                        }
                    }
                }
            }
        }
    }

    private fun sendMessage() {
        if (fromUserRooms == null) {
            fromUserRooms = mutableMapOf()
        }
        fromUserRooms!![roomId] = true
        fromUserObject?.rooms = fromUserRooms
        db.collection("users").document(fromUserObject!!.userId)
            .set(fromUserObject!!, SetOptions.merge())
        db.collection("users").document(targetUserObject?.userId.toString()).collection("contacts").document(fromUserObject?.userId.toString())
            .set(fromUserObject!!, SetOptions.merge())
        db.collection("rooms").document(targetUserObject?.userId.toString())
            .collection("userRooms").document(roomId).set(fromUserObject!!, SetOptions.merge())

        if (targetUserRooms == null) {
            targetUserRooms = mutableMapOf()
        }
        targetUserRooms!![roomId] = true
        targetUserObject?.rooms = targetUserRooms
        db.collection("users").document(targetUserObject?.userId.toString())
            .set(targetUserObject!!, SetOptions.merge())
        db.collection("users").document(fromUserObject!!.userId).collection("contacts").document(targetUserObject?.userId.toString())
            .set(targetUserObject!!, SetOptions.merge())
        db.collection("rooms").document(auth?.uid.toString()).collection("userRooms")
            .document(roomId).set(targetUserObject!!, SetOptions.merge())

        val messageText = binding.messageBodyEditText.text.toString()
        val chatMessage = ChatMessage(messageText, auth?.uid.toString())
        db.collection("messages").document(roomId).collection("roomMessages").add(chatMessage)
        binding.messageBodyEditText.text.clear()
    }

    override fun onStart() {
        super.onStart()
        if (adapter != null)
            adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()

        if (adapter != null) {
            adapter!!.stopListening()
        }
    }

    private inner class ChatMessageViewHolder internal constructor(private val view: View) : RecyclerView.ViewHolder(view) {
        internal fun setMessage(chatMessage: ChatMessage) {
            var chatMessageTextView = view.findViewById<TextView>(R.id.messageTextView)
            var timeTextView = view.findViewById<TextView>(R.id.timeTextView)

            chatMessageTextView.text = chatMessage.messageText

            val splitTime = chatMessage.timeSentAt.toString().split(" ")

            if (splitTime.size > 4) {
                timeTextView.text = "${splitTime[1]} ${splitTime[2]} ${splitTime[3]}"
            }
        }
    }

    private inner class ChatMessageAdapter internal constructor(options: FirestoreRecyclerOptions<ChatMessage>) : FirestoreRecyclerAdapter<ChatMessage, ChatMessageViewHolder>(options) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
            return if (viewType == R.layout.item_message_receiver) {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_receiver, parent, false)
                ChatMessageViewHolder(view)
            }
            else {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_sender, parent, false)
                ChatMessageViewHolder(view)
            }
        }

        override fun onBindViewHolder(viewHolder: ChatMessageViewHolder, p1: Int, chatMessage: ChatMessage) {
            viewHolder.setMessage(chatMessage)
        }

        override fun getItemViewType(position: Int): Int {
            return if (auth?.uid.toString() != getItem(position).fromUserId) {
                R.layout.item_message_receiver
            } else {
                R.layout.item_message_sender
            }
        }

        override fun onDataChanged() {
            binding.messagesRV.scrollToPosition(itemCount - 1)
        }

    }

}