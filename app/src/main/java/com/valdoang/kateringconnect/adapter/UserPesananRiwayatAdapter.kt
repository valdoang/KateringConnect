package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemRiwayatPesananBinding
import com.valdoang.kateringconnect.model.Pesanan
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.utils.withTimestampToDateTimeFormat

class UserPesananRiwayatAdapter(
    private val context: Context
) : RecyclerView.Adapter<UserPesananRiwayatAdapter.MyViewHolder>() {

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
        @RequiresApi(Build.VERSION_CODES.N)
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
                Glide.with(context).load(pesanan.vendorFoto).error(R.drawable.default_vendor_profile).into(ivUserVendor)
                tvPemesananName.text = pesanan.vendorNama

                when (pesanan.status) {
                    context.getString(R.string.status_butuh_konfirmasi_vendor) -> {
                        tvPemesananStatus.text = context.getString(R.string.menunggu_konfirmasi)
                        tvPemesananStatus.setTextColor(context.resources.getColor(R.color.blue))
                        tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_butuh_konfirmasi_bg)
                    }
                    context.getString(R.string.status_butuh_konfirmasi_pengguna) -> {
                        tvPemesananStatus.text = context.getString(R.string.butuh_konfirmasi)
                        tvPemesananStatus.setTextColor(context.resources.getColor(R.color.blue))
                        tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_butuh_konfirmasi_bg)
                    }
                    context.getString(R.string.status_proses) -> {
                        tvPemesananStatus.text = context.getString(R.string.status_proses)
                        tvPemesananStatus.setTextColor(context.resources.getColor(R.color.orange))
                        tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_proses_bg)
                    }
                    context.getString(R.string.status_selesai) -> {
                        tvPemesananStatus.text = context.getString(R.string.status_selesai)
                        tvPemesananStatus.setTextColor(context.resources.getColor(R.color.green_200))
                        tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_selesai_bg)
                    }
                    context.getString(R.string.status_batal) -> {
                        tvPemesananStatus.text = context.getString(R.string.status_batal)
                        tvPemesananStatus.setTextColor(context.resources.getColor(R.color.grey_200))
                        tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_batal_bg)
                    }
                    context.getString(R.string.status_ditolak) -> {
                        tvPemesananStatus.text = context.getString(R.string.status_ditolak)
                        tvPemesananStatus.setTextColor(context.resources.getColor(R.color.red))
                        tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_ditolak_bg)
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(pesananList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Pesanan)
    }
}