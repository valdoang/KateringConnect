package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.valdoang.kateringconnect.databinding.ItemAcKategoriBinding
import com.valdoang.kateringconnect.model.AcKategori

class AcKategoriAdapter : RecyclerView.Adapter<AcKategoriAdapter.MyViewHolder>() {

    private val acKategoriList = ArrayList<AcKategori>()
    private var onItemClickCallback: OnItemClickCallback? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<AcKategori>) {
        acKategoriList.clear()
        acKategoriList.addAll(itemList)
        notifyDataSetChanged()
    }


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemAcKategoriBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(acKategori: AcKategori) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(acKategori)
            }

            binding.apply {
                tvNamaKategori.text = acKategori.nama
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemAcKategoriBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return acKategoriList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(acKategoriList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: AcKategori)
    }
}