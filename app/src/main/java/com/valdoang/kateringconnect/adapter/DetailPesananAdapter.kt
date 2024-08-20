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

class DetailPesananAdapter(
    private val context: Context, private val user: String, private val status: String
) : RecyclerView.Adapter<DetailPesananAdapter.MyViewHolder>() {

    private val menuPesananList = ArrayList<Keranjang>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Keranjang>) {
        menuPesananList.clear()
        menuPesananList.addAll(itemList)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemPesananBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menuPesanan: Keranjang) {
            binding.apply {
                tvJumlahPesanan.text = context.getString(R.string.tv_jumlah_pesanan, menuPesanan.jumlah)
                tvNamaMenu.text = menuPesanan.namaMenu
                tvNamaOpsi.text = menuPesanan.namaOpsi
                tvCatatan.text = menuPesanan.catatan
                tvSubtotal.text = menuPesanan.subtotal?.withNumberingFormat()
                tvEdit.text = context.getString(R.string.tambah_porsi)

                if (menuPesanan.catatan == "") {
                    tvCatatan.visibility = View.GONE
                } else {
                    tvCatatan.visibility = View.VISIBLE
                }

                if (menuPesanan.namaOpsi == "") {
                    tvNamaOpsi.visibility = View.GONE
                } else {
                    tvNamaOpsi.visibility = View.VISIBLE
                }

                when (user) {
                    context.getString(R.string.pembeli) -> tvEdit.visibility = View.VISIBLE
                    context.getString(R.string.vendor) -> tvEdit.visibility = View.GONE
                }

                if (status == context.getString(R.string.selesai) ||  status == context.getString(R.string.batal)) {
                    tvEdit.visibility = View.GONE
                }

                tvEdit.setOnClickListener {
                    TODO("Tambah Porsi")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            ItemPesananBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return menuPesananList.size
    }
    
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(menuPesananList[position])
    }
}