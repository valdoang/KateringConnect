package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemUserBerandaBinding
import com.valdoang.kateringconnect.model.Vendor

class UserBerandaAdapter(
    private val context: Context
) : RecyclerView.Adapter<UserBerandaAdapter.MyViewHolder>() {

    private val vendorList = ArrayList<Vendor>()
    private var onItemClickCallback: OnItemClickCallback? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Vendor>) {
        vendorList.clear()
        vendorList.addAll(itemList)
        notifyDataSetChanged()
    }


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemUserBerandaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(vendor: Vendor) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(vendor)
            }

            binding.apply {
                Glide.with(context).load(vendor.foto).error(R.drawable.default_vendor_profile).into(ivKatering)
                tvKateringName.text = vendor.nama
                tvKateringAddress.text = vendor.alamat
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemUserBerandaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return vendorList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(vendorList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Vendor)
    }
}