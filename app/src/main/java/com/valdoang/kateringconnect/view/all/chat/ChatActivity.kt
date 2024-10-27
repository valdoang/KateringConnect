package com.valdoang.kateringconnect.view.all.chat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.adapter.ChatAdapter
import com.valdoang.kateringconnect.databinding.ActivityChatBinding
import com.valdoang.kateringconnect.model.Chat
import com.valdoang.kateringconnect.utils.Cons

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private var db = Firebase.firestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatList: ArrayList<Chat>
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        chatList = arrayListOf()
        progressBar = binding.progressBar

        setupAction()
        setupView()
        setupChat()
    }

    private fun setupChat() {
        val userId = firebaseAuth.currentUser!!.uid

        progressBar.visibility = View.VISIBLE
        val userRef = db.collection("user").document(userId).collection("chatChannel")
        userRef.addSnapshotListener{ snapshot,_ ->
            if (snapshot != null) {
                chatList.clear()
                for (data in snapshot.documents) {
                    val chat: Chat? = data.toObject(Chat::class.java)
                    if (chat != null) {
                        chat.otherId = data.id
                        chatList.add(chat)
                    }
                }

                chatAdapter.setItems(chatList)
                chatAdapter.setOnItemClickCallback(object :
                    ChatAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: Chat) {
                        val intent = Intent(applicationContext, RoomChatActivity::class.java)
                        intent.putExtra(Cons.EXTRA_ID, data.otherId)
                        startActivity(intent)
                    }
                })

                progressBar.visibility = View.GONE

                if (chatList.isEmpty()) {
                    binding.noDataAnimation.visibility = View.VISIBLE
                    binding.tvNoData.visibility = View.VISIBLE

                }
                else {
                    binding.noDataAnimation.visibility = View.GONE
                    binding.tvNoData.visibility = View.GONE
                }
            }
        }
    }

    private fun setupView() {
        recyclerView = binding.rvChat
        recyclerView.layoutManager = LinearLayoutManager(this)

        chatAdapter = ChatAdapter(this)
        recyclerView.adapter = chatAdapter
        chatAdapter.setItems(chatList)
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}