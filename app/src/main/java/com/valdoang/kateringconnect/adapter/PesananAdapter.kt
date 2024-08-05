package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemPesananBinding
import com.valdoang.kateringconnect.model.Keranjang
import com.valdoang.kateringconnect.utils.withNumberingFormat


class PesananAdapter(
    private val context: Context
) : RecyclerView.Adapter<PesananAdapter.MyViewHolder>() {

    private val pesananList = ArrayList<Keranjang>()
    private var onItemClickCallback: OnItemClickCallback? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Keranjang>) {
        pesananList.clear()
        pesananList.addAll(itemList)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemPesananBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pesanan: Keranjang) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(pesanan)
            }
            binding.apply {
                tvJumlahPesanan.text = context.getString(R.string.tv_jumlah_pesanan, pesanan.jumlah)
                tvNamaMenu.text = pesanan.namaMenu
                tvNamaOpsi.text = pesanan.namaOpsi
                tvCatatan.text = pesanan.catatan
                tvSubtotal.text = pesanan.subtotal?.withNumberingFormat()

                if (pesanan.catatan == "") {
                    tvCatatan.visibility = View.GONE
                } else {
                    tvCatatan.visibility = View.VISIBLE
                }

                if (pesanan.namaOpsi == "") {
                    tvNamaOpsi.visibility = View.GONE
                } else {
                    tvNamaOpsi.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemPesananBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return pesananList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(pesananList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Keranjang)
    }
}