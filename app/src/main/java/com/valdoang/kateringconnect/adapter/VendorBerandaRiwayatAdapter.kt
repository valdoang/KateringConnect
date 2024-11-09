package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemRiwayatPesananBinding
import com.valdoang.kateringconnect.model.Keranjang
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
                var mTambahPorsi = false
                val menuPesananRef = db.collection("pesanan").document(pesanan.id!!).collection("menuPesanan")
                menuPesananRef.addSnapshotListener { menuPesananSnapshot, _ ->
                    if (menuPesananSnapshot != null) {
                        var subtotalTemp = 0L
                        var jumlahTemp = 0
                        for (data in menuPesananSnapshot) {
                            val subtotal = data.get("subtotal").toString().toLong()
                            val jumlah = data.get("jumlah").toString().toInt()
                            val tambahPorsi = data.get("tambahPorsi")
                            if (tambahPorsi == true) {
                                mTambahPorsi = true
                            }
                            subtotalTemp += subtotal
                            jumlahTemp += jumlah
                        }

                        val sTotalHarga = subtotalTemp + pesanan.ongkir!!.toLong()

                        tvPemesananMenu.text = context.getString(R.string.jumlah_alamat, jumlahTemp.toString(), context.getString(R.string.tv_address_city, pesanan.userAlamat, pesanan.userKota))
                        tvPemesananTotal.text = context.getString(R.string.total_metodepembayaran, sTotalHarga.withNumberingFormat(), pesanan.metodePembayaran)
                    }

                    when (pesanan.status) {
                        context.getString(R.string.status_butuh_konfirmasi_vendor) -> {
                            tvPemesananStatus.text = context.getString(R.string.butuh_konfirmasi)
                            tvPemesananStatus.setTextColor(context.resources.getColor(R.color.blue))
                            tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_butuh_konfirmasi_bg)

                            if (mTambahPorsi) {
                                tvPorsiAdd.visibility = View.VISIBLE
                            } else {
                                tvPorsiAdd.visibility = View.GONE
                            }
                        }
                        context.getString(R.string.status_butuh_konfirmasi_pengguna) -> {
                            tvPemesananStatus.text = context.getString(R.string.menunggu_konfirmasi)
                            tvPemesananStatus.setTextColor(context.resources.getColor(R.color.blue))
                            tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_butuh_konfirmasi_bg)
                        }
                        context.getString(R.string.status_proses) -> {
                            tvPemesananStatus.text = context.getString(R.string.status_proses)
                            tvPemesananStatus.setTextColor(context.resources.getColor(R.color.orange))
                            tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_proses_bg)

                            if (mTambahPorsi) {
                                tvPorsiAdd.visibility = View.VISIBLE
                            } else {
                                tvPorsiAdd.visibility = View.GONE
                            }
                        }
                        context.getString(R.string.status_selesai) -> {
                            tvPemesananStatus.text = context.getString(R.string.status_selesai)
                            tvPemesananStatus.setTextColor(context.resources.getColor(R.color.green_200))
                            tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_selesai_bg)
                            tvPorsiAdd.visibility = View.GONE
                        }
                        context.getString(R.string.status_batal) -> {
                            tvPemesananStatus.text = context.getString(R.string.status_batal)
                            tvPemesananStatus.setTextColor(context.resources.getColor(R.color.grey_200))
                            tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_batal_bg)
                            tvPorsiAdd.visibility = View.GONE
                        }
                        context.getString(R.string.status_ditolak) -> {
                            tvPemesananStatus.text = context.getString(R.string.menolak)
                            tvPemesananStatus.setTextColor(context.resources.getColor(R.color.red))
                            tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_ditolak_bg)
                            tvPorsiAdd.visibility = View.GONE
                        }
                    }
                }

                tvPemesananDate.text = pesanan.jadwal?.withTimestampToDateTimeFormat()
                Glide.with(context).load(pesanan.userFoto).error(R.drawable.default_profile).into(ivUserVendor)
                val userRef = db.collection("user").document(pesanan.userId!!)
                userRef.get().addOnSuccessListener {
                    if (it != null) {
                        val namaUser = it.data?.get("nama").toString()
                        tvPemesananName.text = namaUser
                    }
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