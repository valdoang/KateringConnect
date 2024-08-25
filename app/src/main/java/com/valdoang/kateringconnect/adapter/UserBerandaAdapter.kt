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
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemUserBerandaBinding
import com.valdoang.kateringconnect.model.Vendor
import com.valdoang.kateringconnect.utils.roundOffDecimal
import com.valdoang.kateringconnect.utils.withNumberingFormat

class UserBerandaAdapter(
    private val context: Context
) : RecyclerView.Adapter<UserBerandaAdapter.MyViewHolder>() {

    private val vendorList = ArrayList<Vendor>()
    private var onItemClickCallback: OnItemClickCallback? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Vendor>) {
        vendorList.clear()
        vendorList.addAll(itemList)
        notifyDataSetChanged()
    }


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemUserBerandaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(vendor: Vendor) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(vendor)
            }

            binding.apply {
                Glide.with(context).load(vendor.foto).error(R.drawable.default_vendor_profile).into(ivKatering)
                tvKateringName.text = vendor.nama
                tvKateringOngkir.text = context.getString(R.string.rupiah_text, vendor.ongkir?.withNumberingFormat())

                if (vendor.sizeNilai == 0) {
                    ivKateringStar.visibility = View.GONE
                    tvKateringStar.visibility = View.GONE
                }
                else {
                    ivKateringStar.visibility = View.VISIBLE
                    tvKateringStar.visibility = View.VISIBLE
                    tvKateringStar.text = context.getString(R.string.vendor_star, vendor.nilai?.roundOffDecimal(), vendor.sizeNilai.toString())
                }

                if (vendor.kategoriMenu?.isEmpty() == true) {
                    ivKateringKategori.visibility = View.GONE
                    tvKateringKategori.visibility = View.GONE
                } else {
                    ivKateringKategori.visibility = View.VISIBLE
                    tvKateringKategori.visibility = View.VISIBLE
                    tvKateringKategori.text = vendor.kategoriMenu
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemUserBerandaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return vendorList.size
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(vendorList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Vendor)
    }
}