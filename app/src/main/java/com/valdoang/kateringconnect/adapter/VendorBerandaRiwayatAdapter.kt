package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemVendorBerandaRiwayatBinding
import com.valdoang.kateringconnect.model.Pesanan
import com.valdoang.kateringconnect.utils.withTimestampToDateTimeFormat

class VendorBerandaRiwayatAdapter(
    private val context: Context
) : RecyclerView.Adapter<VendorBerandaRiwayatAdapter.MyViewHolder>() {

    private val pesananList = ArrayList<Pesanan>()
    private var onItemClickCallback: OnItemClickCallback? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Pesanan>) {
        pesananList.clear()
        pesananList.addAll(itemList)
        notifyDataSetChanged()
    }


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemVendorBerandaRiwayatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pesanan: Pesanan) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(pesanan)
            }

            binding.apply {
                tvPesananJumlah.text = context.getString(R.string.riwayat_jumlah, pesanan.jumlah)
                tvPesananMenu.text = pesanan.menuNama
                tvPesananDate.text = pesanan.jadwal?.withTimestampToDateTimeFormat()
                Glide.with(context).load(pesanan.fotoUser).error(R.drawable.default_profile).into(ivUser)
                tvPesananName.text = pesanan.userNama
                tvPesananAddress.text = context.getString(R.string.tv_address_city, pesanan.userAlamat, pesanan.userKota)
                if (pesanan.status == context.getString(R.string.status_proses)) {
                    tvPesananStatus.visibility = View.GONE
                } else if (pesanan.status == context.getString(R.string.status_selesai)) {
                    tvPesananStatus.text = context.getString(R.string.status_selesai)
                    tvPesananStatus.setTextColor(context.resources.getColor(R.color.green_200))
                    tvPesananStatus.background = context.resources.getDrawable(R.drawable.status_selesai_bg)
                } else if (pesanan.status == context.getString(R.string.status_batal)) {
                    tvPesananStatus.text = context.getString(R.string.status_batal)
                    tvPesananStatus.setTextColor(context.resources.getColor(R.color.grey_200))
                    tvPesananStatus.background = context.resources.getDrawable(R.drawable.status_batal_bg)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            ItemVendorBerandaRiwayatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return pesananList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(pesananList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Pesanan)
    }
}