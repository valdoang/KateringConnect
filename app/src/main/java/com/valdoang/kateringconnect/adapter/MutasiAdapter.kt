package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemMutasiBinding
import com.valdoang.kateringconnect.model.Mutasi
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.utils.withTimestampToDateTimeFormat

class MutasiAdapter(
    private val context: Context
) : RecyclerView.Adapter<MutasiAdapter.MyViewHolder>() {

    private val mutasiList = ArrayList<Mutasi>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Mutasi>) {
        mutasiList.clear()
        mutasiList.addAll(itemList)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemMutasiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mutasi: Mutasi) {
            binding.apply {
                tvTanggal.text = mutasi.tanggal?.withTimestampToDateTimeFormat()
                tvIdMutasi.text = mutasi.id
                tvKeterangan.text = mutasi.keterangan
                when (mutasi.jenis) {
                    context.getString(R.string.kredit) -> {
                        tvNominal.text = context.getString(R.string.mutasi_kredit, mutasi.nominal?.withNumberingFormat())
                        tvNominal.setTextColor(context.resources.getColor(R.color.green))
                    }
                    context.getString(R.string.debit) -> {
                        tvNominal.text = context.getString(R.string.mutasi_debit, mutasi.nominal?.withNumberingFormat())
                        tvNominal.setTextColor(context.resources.getColor(R.color.red))
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemMutasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mutasiList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(mutasiList[position])
    }
}