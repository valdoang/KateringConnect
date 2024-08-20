package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemRiwayatPesananBinding
import com.valdoang.kateringconnect.model.Pesanan
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.utils.withTimestampToDateTimeFormat

class VendorBerandaRiwayatAdapter(
    private val context: Context
) : RecyclerView.Adapter<VendorBerandaRiwayatAdapter.MyViewHolder>() {

    private val pesananList = ArrayList<Pesanan>()
    private var onItemClickCallback: OnItemClickCallback? = null
    private var db = Firebase.firestore

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Pesanan>) {
        pesananList.clear()
        pesananList.addAll(itemList)
        notifyDataSetChanged()
    }


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemRiwayatPesananBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pesanan: Pesanan) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(pesanan)
            }

            binding.apply {
                val menuPesananRef = db.collection("pesanan").document(pesanan.id!!).collection("menuPesanan")
                menuPesananRef.addSnapshotListener { menuPesananSnapshot, _ ->
                    if (menuPesananSnapshot != null) {
                        var subtotalTemp = 0L
                        var jumlahTemp = 0
                        for (data in menuPesananSnapshot) {
                            val subtotal = data.get("subtotal").toString().toLong()
                            val jumlah = data.get("jumlah").toString().toInt()
                            subtotalTemp += subtotal
                            jumlahTemp += jumlah
                        }

                        val sTotalHarga = subtotalTemp + pesanan.ongkir!!.toLong()

                        tvPemesananMenu.text = context.getString(R.string.jumlah_alamat, jumlahTemp.toString(), context.getString(R.string.tv_address_city, pesanan.userAlamat, pesanan.userKota))
                        tvPemesananTotal.text = sTotalHarga.withNumberingFormat()
                    }
                }

                tvPemesananDate.text = pesanan.jadwal?.withTimestampToDateTimeFormat()
                Glide.with(context).load(pesanan.vendorFoto).error(R.drawable.default_profile).into(ivUserVendor)
                tvPemesananName.text = pesanan.vendorNama

                if (pesanan.status == context.getString(R.string.status_proses)) {
                    tvPemesananStatus.visibility = View.GONE
                } else if (pesanan.status == context.getString(R.string.status_selesai)) {
                    tvPemesananStatus.text = context.getString(R.string.status_selesai)
                    tvPemesananStatus.setTextColor(context.resources.getColor(R.color.green_200))
                    tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_selesai_bg)
                } else if (pesanan.status == context.getString(R.string.status_batal)) {
                    tvPemesananStatus.text = context.getString(R.string.status_batal)
                    tvPemesananStatus.setTextColor(context.resources.getColor(R.color.grey_200))
                    tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_batal_bg)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            ItemRiwayatPesananBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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