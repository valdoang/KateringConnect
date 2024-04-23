package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.valdoang.kateringconnect.databinding.ItemMenuBinding
import com.valdoang.kateringconnect.databinding.ItemVendorGaleriBinding
import com.valdoang.kateringconnect.model.Gallery
import com.valdoang.kateringconnect.model.Menu

class MenuAdapter(
    private val context: Context
) : RecyclerView.Adapter<MenuAdapter.MyViewHolder>() {

    private val menuList = ArrayList<Menu>()
    private var onItemClickCallback: OnItemClickCallback? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Menu>) {
        menuList.clear()
        menuList.addAll(itemList)
        notifyDataSetChanged()
    }


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: Menu) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(menu)
            }

            binding.apply {
                Glide.with(context).load(menu.foto).into(ivMenu)
                tvMenuName.text = menu.nama
                tvMenuPrice.text = menu.harga
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(menuList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Menu)
    }
}