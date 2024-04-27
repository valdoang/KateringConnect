package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemUserRiwayatBinding
import com.valdoang.kateringconnect.model.Riwayat
import com.valdoang.kateringconnect.utils.withTimestampToDateTimeFormat
import com.valdoang.kateringconnect.view.user.pemesanan.PemesananActivity

class UserRiwayatAdapter(
    private val context: Context
) : RecyclerView.Adapter<UserRiwayatAdapter.MyViewHolder>() {

    private val riwayatList = ArrayList<Riwayat>()
    private var onItemClickCallback: OnItemClickCallback? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Riwayat>) {
        riwayatList.clear()
        riwayatList.addAll(itemList)
        notifyDataSetChanged()
    }


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemUserRiwayatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(riwayat: Riwayat) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(riwayat)
            }

            binding.apply {
                tvPemesananDate.text = riwayat.jadwal?.withTimestampToDateTimeFormat()
                tvPemesananName.text = riwayat.vendorNama
                tvPemesananJumlah.text = itemView.context.getString(R.string.riwayat_jumlah, riwayat.jumlah)
                tvPemesananMenu.text = riwayat.menuNama
                tvPemesananTotal.text = riwayat.totalPembayaran
                if (riwayat.status == context.getString(R.string.status_proses)) {
                    tvPemesananStatus.text = context.getString(R.string.status_proses)
                    tvPemesananStatus.setTextColor(context.resources.getColor(R.color.orange))
                    tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_proses_bg)
                } else if (riwayat.status == context.getString(R.string.status_selesai)) {
                    tvPemesananStatus.text = context.getString(R.string.status_selesai)
                    tvPemesananStatus.setTextColor(context.resources.getColor(R.color.green_200))
                    tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_selesai_bg)
                }

                btnPesanLagi.setOnClickListener {
                    val intent = Intent(context, PemesananActivity::class.java)
                    intent.putExtra(PemesananActivity.EXTRA_ID, riwayat.menuId)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            ItemUserRiwayatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return riwayatList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(riwayatList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Riwayat)
    }
}