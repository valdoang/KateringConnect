package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.valdoang.kateringconnect.databinding.ItemVendorMenuBinding
import com.valdoang.kateringconnect.model.Menu
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.withCurrencyFormat
import com.valdoang.kateringconnect.view.vendor.menu.edit.EditMenuActivity

class MenuAdapter(
    private val context: Context, private val kategoriMenuId: String
) : RecyclerView.Adapter<MenuAdapter.MyViewHolder>() {

    private val menuList = ArrayList<Menu>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Menu>) {
        menuList.clear()
        menuList.addAll(itemList)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemVendorMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: Menu) {
            binding.apply {
                Glide.with(context).load(menu.foto).into(ivMenu)
                tvMenuName.text = menu.nama
                tvMenuPrice.text = menu.harga?.withCurrencyFormat()
                tvEdit.setOnClickListener {
                    val intent = Intent(context, EditMenuActivity::class.java)
                    intent.putExtra(Cons.EXTRA_ID, kategoriMenuId)
                    intent.putExtra(Cons.EXTRA_SEC_ID, menu.id)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemVendorMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(menuList[position])
    }
}