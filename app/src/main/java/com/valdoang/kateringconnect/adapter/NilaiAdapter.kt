package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemNilaiBinding
import com.valdoang.kateringconnect.model.Nilai
import com.valdoang.kateringconnect.utils.withTimestampToDateTimeFormat

class NilaiAdapter(
    private val context: Context
) : RecyclerView.Adapter<NilaiAdapter.MyViewHolder>() {

    private val nilaiList = ArrayList<Nilai>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Nilai>) {
        nilaiList.clear()
        nilaiList.addAll(itemList)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemNilaiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(nilai: Nilai) {
            binding.apply {
                Glide.with(context).load(nilai.userFoto).error(R.drawable.default_profile).into(ivUser)
                tvUserName.text = nilai.userNama
                rbStar.rating = nilai.nilai!!.toFloat()
                tvUlasan.text = nilai.ulasan
                tvMenu.text = context.getString(R.string.menu_dipesan, nilai.menuNama)
                tvTanggal.text = nilai.tanggal!!.withTimestampToDateTimeFormat()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemNilaiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return nilaiList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(nilaiList[position])
    }
}