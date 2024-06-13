package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.valdoang.kateringconnect.databinding.ItemOpsiChooseMenuBinding
import com.valdoang.kateringconnect.model.Menu
import com.valdoang.kateringconnect.utils.withCurrencyFormat


class OpsiChooseMenuAdapter(
    private val context: Context, private var arrayMenuId: ArrayList<String>
) : RecyclerView.Adapter<OpsiChooseMenuAdapter.MyViewHolder>() {

    private val menuList = ArrayList<Menu>()
    private var _checkAccumulator = MutableLiveData(0)
    val checkAccumulator: LiveData<Int> = _checkAccumulator

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Menu>) {
        menuList.clear()
        menuList.addAll(itemList)
        notifyDataSetChanged()
    }


    inner class MyViewHolder(private val binding: ItemOpsiChooseMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: Menu) {
            binding.apply {
                Glide.with(context).load(menu.foto).into(ivMenu)
                tvMenuName.text = menu.nama
                tvMenuPrice.text = menu.harga?.withCurrencyFormat()

                if (arrayMenuId.contains(menu.id)) {
                    checkBox.isChecked = true
                    _checkAccumulator.value = _checkAccumulator.value?.plus(1)
                }

                val checkListener =
                    CompoundButton.OnCheckedChangeListener { _, isChecked ->
                        _checkAccumulator.value = _checkAccumulator.value?.plus(if (isChecked) 1 else -1)
                        val menuId = menu.id
                        if (isChecked) {
                            if (!arrayMenuId.contains(menuId)){
                                if (menuId != null) {
                                    arrayMenuId.add(menuId)
                                }
                            }
                        } else {
                            if (arrayMenuId.contains(menuId)){
                                if (menuId != null) {
                                    arrayMenuId.remove(menuId)
                                }
                            }
                        }
                    }

                checkBox.setOnCheckedChangeListener(checkListener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemOpsiChooseMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(menuList[position])
    }
}