package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemVendorOpsiBinding
import com.valdoang.kateringconnect.model.Opsi
import com.valdoang.kateringconnect.utils.withNumberingFormat

class VendorOpsiAdapter(
    private val context: Context
)    : RecyclerView.Adapter<VendorOpsiAdapter.MyViewHolder>() {

    private val opsiList = ArrayList<Opsi>()
    private var onItemClickCallback: OnItemClickCallback? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Opsi>) {
        opsiList.clear()
        opsiList.addAll(itemList)
        notifyDataSetChanged()
    }


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemVendorOpsiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(opsi: Opsi) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(opsi)
            }

            binding.apply {
                tvNamaOpsi.text = opsi.nama
                tvHargaOpsi.text = context.getString(R.string.harga_opsi, opsi.harga?.withNumberingFormat())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            ItemVendorOpsiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return opsiList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(opsiList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Opsi)
    }
}