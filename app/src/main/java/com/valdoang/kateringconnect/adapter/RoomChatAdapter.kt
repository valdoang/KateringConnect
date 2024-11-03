package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.valdoang.kateringconnect.databinding.ItemRoomChatBinding
import com.valdoang.kateringconnect.model.RoomChat
import com.valdoang.kateringconnect.utils.withTimestampToDateTimeFormat2
import com.valdoang.kateringconnect.utils.withTimestamptoDateFormat
import com.valdoang.kateringconnect.utils.withTimestamptoTimeFormat

class RoomChatAdapter : RecyclerView.Adapter<RoomChatAdapter.MyViewHolder>() {

    private val roomChatList = ArrayList<RoomChat>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<RoomChat>) {
        roomChatList.clear()
        roomChatList.addAll(itemList)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemRoomChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(roomChat: RoomChat) {
            binding.apply {
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                if (position == 0) {
                    tvDay.visibility = View.VISIBLE
                } else {
                    if ((roomChat.tanggal!!.withTimestamptoDateFormat() == roomChatList[position-1].tanggal.toString().withTimestamptoDateFormat())){
                        tvDay.visibility = View.GONE
                    } else {
                        tvDay.visibility = View.VISIBLE
                    }
                }

                tvDay.text = roomChat.tanggal!!.withTimestamptoDateFormat()
                if (roomChat.userId != userId) {
                    clMessageOur.visibility = View.GONE
                    clMessageOther.visibility = View.VISIBLE
                    tvMessageOther.text = roomChat.pesan
                    tvTimeOther.text = roomChat.tanggal?.withTimestamptoTimeFormat()
                } else {
                    clMessageOther.visibility = View.GONE
                    clMessageOur.visibility = View.VISIBLE
                    tvMessageOur.text = roomChat.pesan
                    tvTimeOur.text = roomChat.tanggal?.withTimestamptoTimeFormat()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemRoomChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return roomChatList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(roomChatList[position])
    }
}