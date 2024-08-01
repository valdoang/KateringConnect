package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.databinding.ItemAddOrEditKeranjangBinding
import com.valdoang.kateringconnect.model.Keranjang
import com.valdoang.kateringconnect.utils.withNumberingFormat


class AddOrEditKeranjangAdapter(
    private val context: Context, private val vendorId: String
) : RecyclerView.Adapter<AddOrEditKeranjangAdapter.MyViewHolder>() {

    private var keranjangList: ArrayList<Keranjang> = ArrayList()
    private var onItemClickCallback: OnItemClickCallback? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(keranjang: List<Keranjang>) {
        keranjangList.clear()
        keranjangList.addAll(keranjang)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemAddOrEditKeranjangBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(keranjang: Keranjang) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(keranjang)
            }
            binding.apply {
                Glide.with(context).load(keranjang.foto).into(ivMenu)
                tvMenuName.text = keranjang.namaMenu
                tvNamaOpsi.text = keranjang.namaOpsi
                tvMenuPrice.text = keranjang.subtotal?.withNumberingFormat()
                tvJumlah.text = keranjang.jumlah
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemAddOrEditKeranjangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return keranjangList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(keranjangList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Keranjang)
    }
}