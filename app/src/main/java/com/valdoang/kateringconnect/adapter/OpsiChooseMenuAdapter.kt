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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.databinding.ItemOpsiChooseMenuBinding
import com.valdoang.kateringconnect.model.Menu
import com.valdoang.kateringconnect.utils.withCurrencyFormat


class OpsiChooseMenuAdapter(
    private val context: Context, private var arrayMenuId: ArrayList<String>, private var grupOpsiId: String, private var btnSimpan: Button,
    private var arrayMenu: ArrayList<Menu>
) : RecyclerView.Adapter<OpsiChooseMenuAdapter.MyViewHolder>() {

    private val menuList = ArrayList<Menu>()
    private var _checkAccumulator = MutableLiveData(0)
    val checkAccumulator: LiveData<Int> = _checkAccumulator
    private var arrayMenuIdTemp: ArrayList<String> = ArrayList()
    private var userId = FirebaseAuth.getInstance().currentUser!!.uid
    private var db = Firebase.firestore

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
                                    arrayMenu.add(menu)
                                }
                            }
                        } else {
                            if (arrayMenuId.contains(menuId)){
                                if (menuId != null) {
                                    arrayMenuId.remove(menuId)
                                    arrayMenu.remove(menu)
                                }
                            }
                        }

                        val ref = db.collection("user").document(userId).collection("grupOpsi").document(grupOpsiId)
                        ref.get().addOnSuccessListener {  grupOpsi ->
                            if (grupOpsi != null) {
                                val menuIdTemp = grupOpsi.data?.get("menuId") as? ArrayList<String>
                                if (menuIdTemp != null) {
                                    arrayMenuIdTemp = menuIdTemp
                                }
                            }
                        }

                        btnSimpan.isEnabled = arrayMenuIdTemp != arrayMenuId
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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}