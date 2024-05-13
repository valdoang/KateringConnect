package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemChatBinding
import com.valdoang.kateringconnect.model.Chat
import com.valdoang.kateringconnect.utils.withTimestampToDateTimeFormat2
import com.valdoang.kateringconnect.utils.withTimestamptoTimeFormat

class ChatAdapter(
    private val context: Context
) : RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    private val chatList = ArrayList<Chat>()
    private var onItemClickCallback: OnItemClickCallback? = null
    private var db = Firebase.firestore

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Chat>) {
        chatList.clear()
        chatList.addAll(itemList)
        notifyDataSetChanged()
    }


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(chat)
            }

            binding.apply {
                val otherId = chat.otherId
                val channelId = chat.channelId

                val otherRef = db.collection("user").document(otherId!!)
                otherRef.get().addOnSuccessListener {  document ->
                    if (document != null) {
                        val foto = document.data?.get("foto").toString()
                        val nama = document.data?.get("nama").toString()
                        val jenis = document.data?.get("jenis").toString()

                        if (jenis == context.getString(R.string.pembeli)) {
                            Glide.with(context).load(foto).error(R.drawable.default_profile).into(ivUser)
                        } else if (jenis == context.getString(R.string.vendor)) {
                            Glide.with(context).load(foto).error(R.drawable.default_vendor_profile).into(ivUser)
                        }

                        tvUserName.text = nama
                    }
                }

                val channelRef = db.collection("chat").document(channelId!!).collection("message")
                channelRef.orderBy("tanggal", Query.Direction.DESCENDING).limit(1)
                    .addSnapshotListener { snapshot,_ ->
                        if (snapshot != null) {
                            for (document in snapshot.documents) {
                                val message = document.data?.get("pesan").toString()
                                val time = document.data?.get("tanggal").toString()

                                tvMessage.text = message
                                tvTime.text = DateUtils.getRelativeTimeSpanString(time.toLong())
                            }
                        }
                    }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Chat)
    }
}