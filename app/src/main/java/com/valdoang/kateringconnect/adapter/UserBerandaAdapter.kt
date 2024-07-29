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
import com.valdoang.kateringconnect.databinding.ItemUserBerandaBinding
import com.valdoang.kateringconnect.model.Star
import com.valdoang.kateringconnect.model.Vendor
import com.valdoang.kateringconnect.utils.roundOffDecimal
import com.valdoang.kateringconnect.utils.withNumberingFormat

class UserBerandaAdapter(
    private val context: Context
) : RecyclerView.Adapter<UserBerandaAdapter.MyViewHolder>() {

    private val vendorList = ArrayList<Vendor>()
    private var onItemClickCallback: OnItemClickCallback? = null
    private var starList: ArrayList<Star> = ArrayList()
    private var totalNilai = 0.0
    private var db = Firebase.firestore

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

                val nilaiRef = db.collection("nilai").whereEqualTo("vendorId", vendor.id)
                nilaiRef.addSnapshotListener { snapshot,_ ->
                    if (snapshot != null) {
                        starList.clear()
                        totalNilai = 0.0
                        for (data in snapshot.documents) {
                            val star: Star? = data.toObject(Star::class.java)
                            if (star != null) {
                                starList.add(star)
                            }
                        }

                        for (i in starList) {
                            val nilai = i.nilai?.toDouble()
                            if (nilai != null) {
                                totalNilai += nilai
                            }
                        }

                        val sizeNilai = starList.size
                        val nilaiStar = totalNilai/sizeNilai

                        if (sizeNilai == 0) {
                            ivKateringStar.visibility = View.GONE
                            tvKateringStar.visibility = View.GONE
                        }
                        else {
                            ivKateringStar.visibility = View.VISIBLE
                            tvKateringStar.visibility = View.VISIBLE
                            tvKateringStar.text = context.getString(R.string.vendor_star, nilaiStar.roundOffDecimal(), sizeNilai.toString().withNumberingFormat())
                        }

                    }
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