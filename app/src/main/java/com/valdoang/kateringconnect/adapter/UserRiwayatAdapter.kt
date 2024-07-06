package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemUserRiwayatBinding
import com.valdoang.kateringconnect.model.Pesanan
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.utils.withTimestampToDateTimeFormat
import com.valdoang.kateringconnect.view.user.custommenu.CustomMenuActivity

class UserRiwayatAdapter(
    private val context: Context
) : RecyclerView.Adapter<UserRiwayatAdapter.MyViewHolder>() {

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

    inner class MyViewHolder(private val binding: ItemUserRiwayatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(pesanan: Pesanan) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(pesanan)
            }

            binding.apply {
                tvPemesananDate.text = pesanan.jadwal?.withTimestampToDateTimeFormat()
                Glide.with(context).load(pesanan.vendorFoto).error(R.drawable.default_vendor_profile).into(ivVendor)
                tvPemesananName.text = pesanan.vendorNama
                tvPemesananJumlah.text = context.getString(R.string.riwayat_jumlah, pesanan.jumlah)
                tvPemesananMenu.text = pesanan.menuNama
                tvPemesananTotal.text = pesanan.totalHarga?.withNumberingFormat()
                if (pesanan.status == context.getString(R.string.status_proses)) {
                    tvPemesananStatus.text = context.getString(R.string.status_proses)
                    tvPemesananStatus.setTextColor(context.resources.getColor(R.color.orange))
                    tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_proses_bg)
                    btnPesanLagi.visibility = View.GONE
                } else if (pesanan.status == context.getString(R.string.status_selesai)) {
                    tvPemesananStatus.text = context.getString(R.string.status_selesai)
                    tvPemesananStatus.setTextColor(context.resources.getColor(R.color.green_200))
                    tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_selesai_bg)
                    btnPesanLagi.visibility = View.VISIBLE
                } else if (pesanan.status == context.getString(R.string.status_batal)) {
                    tvPemesananStatus.text = context.getString(R.string.status_batal)
                    tvPemesananStatus.setTextColor(context.resources.getColor(R.color.grey_200))
                    tvPemesananStatus.background = context.resources.getDrawable(R.drawable.status_batal_bg)
                    btnPesanLagi.visibility = View.VISIBLE
                }

                btnPesanLagi.setOnClickListener {
                    val intent = Intent(context, CustomMenuActivity::class.java)
                    intent.putExtra(Cons.EXTRA_ID, pesanan.vendorId)
                    intent.putExtra(Cons.EXTRA_SEC_ID, pesanan.kategoriId)
                    intent.putExtra(Cons.EXTRA_THIRD_ID, pesanan.menuId)
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