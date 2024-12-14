package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemVerifikasiBinding
import com.valdoang.kateringconnect.model.User

class VerifikasiAdapter(val context: Context) : RecyclerView.Adapter<VerifikasiAdapter.MyViewHolder>() {

    private val userList = ArrayList<User>()
    private var onItemClickCallback: OnItemClickCallback? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<User>) {
        userList.clear()
        userList.addAll(itemList)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemVerifikasiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(user)
            }
            binding.apply {
                tvNamaUser.text = user.nama
                when(user.jenis) {
                    context.getString(R.string.pembeli) -> {
                        Glide.with(context).load(R.drawable.default_profile).into(ivUser)
                    }
                    context.getString(R.string.vendor) -> {
                        Glide.with(context).load(R.drawable.default_vendor_profile).into(ivUser)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemVerifikasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: User)
    }
}