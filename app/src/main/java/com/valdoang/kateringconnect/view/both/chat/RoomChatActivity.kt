package com.valdoang.kateringconnect.view.both.chat

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.RoomChatAdapter
import com.valdoang.kateringconnect.databinding.ActivityRoomChatBinding
import com.valdoang.kateringconnect.model.RoomChat

class RoomChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoomChatBinding
    private var otherId: String? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var ivUser: ImageView
    private lateinit var tvNama: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var roomChatAdapter: RoomChatAdapter
    private lateinit var roomChatList: ArrayList<RoomChat>
    private var channelId = ""
    private var ourId = ""
    private lateinit var etPesan: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        roomChatList = arrayListOf()

        ourId = firebaseAuth.currentUser!!.uid
        otherId = intent.getStringExtra(EXTRA_ID)

        ivUser = binding.ivUserPhoto
        tvNama = binding.tvUserName
        etPesan = binding.edMessage

        setupOther(otherId!!)
        setupView()
        setupAction()
        getChannel(otherId!!)
        sendMessage(otherId!!)
    }

    private fun setupOther(otherId: String) {
        val otherRef = db.collection("user").document(otherId)
        otherRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val foto = document.data?.get("foto").toString()
                val nama = document.data?.get("nama").toString()
                val jenis = document.data?.get("jenis").toString()

                if (jenis == getString(R.string.pembeli)) {
                    Glide.with(applicationContext).load(foto).error(R.drawable.default_profile).into(ivUser)
                }
                else if (jenis == getString(R.string.vendor)) {
                    Glide.with(applicationContext).load(foto).error(R.drawable.default_vendor_profile).into(ivUser)
                }

                tvNama.text = nama
            }
        }
    }

    private fun setupView() {
        recyclerView = binding.rvRoomChat
        val llm = LinearLayoutManager(this)
        llm.stackFromEnd = true
        llm.reverseLayout = false
        recyclerView.layoutManager = llm

        roomChatAdapter = RoomChatAdapter()
        recyclerView.adapter = roomChatAdapter
        roomChatAdapter.setItems(roomChatList)
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getChannel(otherId: String) {
        val userRef = db.collection("user").document(ourId).collection("chatChannel").document(otherId)
        userRef.get().addOnSuccessListener { document ->
            if (document != null) {
                channelId = document.data?.get("channelId").toString()
                setupMessage()
            }
        }
    }

    private fun setupMessage() {
        val channelRef = db.collection("chat").document(channelId).collection("message")
        channelRef.addSnapshotListener { snapshot,_ ->
            if (snapshot != null) {
                roomChatList.clear()
                for (data in snapshot.documents) {
                    val roomChat: RoomChat? = data.toObject(RoomChat::class.java)
                    if (roomChat != null) {
                        roomChat.id = data.id
                        roomChatList.add(roomChat)
                    }
                }

                roomChatList.sortBy { roomChat ->
                    roomChat.tanggal
                }

                recyclerView.scrollToPosition(roomChatList.size-1)
                roomChatAdapter.setItems(roomChatList)
            }
        }
    }


    private fun sendMessage(otherId: String) {
        binding.ibSend.setOnClickListener {
            val sTanggal = System.currentTimeMillis().toString()
            val sPesan = etPesan.text.toString().trim()

            val pesanMap = mapOf(
                "userId" to ourId,
                "pesan" to sPesan,
                "tanggal" to sTanggal
            )

            if (channelId == "null") {
                val newChannel = db.collection("chat").document()
                newChannel.set(mapOf("userIds" to listOf(ourId, otherId)))
                channelId = newChannel.id

                val ourRef = db.collection("user").document(ourId).collection("chatChannel").document(otherId)
                ourRef.set(mapOf("channelId" to channelId))

                val otherRef = db.collection("user").document(otherId).collection("chatChannel").document(ourId)
                otherRef.set(mapOf("channelId" to channelId))

                val messageRef = db.collection("chat").document(channelId).collection("message").document()
                messageRef.set(pesanMap).addOnSuccessListener {
                    etPesan.text = null
                    setupMessage()
                } .addOnFailureListener {
                    Toast.makeText(this, "Gagal Mengirim Pesan", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                val messageRef = db.collection("chat").document(channelId).collection("message").document()
                messageRef.set(pesanMap).addOnSuccessListener {
                    etPesan.text = null
                    setupMessage()
                } .addOnFailureListener {
                    Toast.makeText(this, "Gagal Mengirim Pesan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}