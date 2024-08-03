package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemKeranjangBinding
import com.valdoang.kateringconnect.model.AllKeranjang
import com.valdoang.kateringconnect.model.Keranjang


class AllKeranjangAdapter(
    private val context: Context, private val edit: Boolean, private val pilihSemua: Boolean
) : RecyclerView.Adapter<AllKeranjangAdapter.MyViewHolder>() {

    private val allKeranjangList = ArrayList<AllKeranjang>()
    private var onItemClickCallback: OnItemClickCallback? = null
    private var db = Firebase.firestore
    private var userId = FirebaseAuth.getInstance().currentUser!!.uid
    private var keranjangList: ArrayList<Keranjang> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<AllKeranjang>) {
        allKeranjangList.clear()
        allKeranjangList.addAll(itemList)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemKeranjangBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(allKeranjang: AllKeranjang) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(allKeranjang)
            }
            binding.apply {
                val vendorRef = db.collection("user").document(allKeranjang.vendorId!!)
                vendorRef.get().addOnSuccessListener { vendorSnapshot ->
                    if (vendorSnapshot != null) {
                        val namaVendor = vendorSnapshot.data?.get("nama").toString()
                        tvNamaVendor.text = namaVendor
                    }
                }

                val keranjangRef = db.collection("user").document(userId).collection("keranjang").document(allKeranjang.vendorId!!).collection("pesanan")
                keranjangRef.get().addOnSuccessListener { keranjangSnapshot ->
                    if (keranjangSnapshot != null) {
                        keranjangList.clear()
                        for (keranjangData in keranjangSnapshot.documents) {
                            val keranjang: Keranjang? = keranjangData.toObject(Keranjang::class.java)
                            if (keranjang != null) {
                                keranjang.id = keranjangData.id
                                keranjangList.add(keranjang)
                            }
                        }
                        val lastMenuFoto = keranjangList[keranjangList.size.minus(1)].foto
                        Glide.with(context).load(lastMenuFoto).into(ivMenu)
                        tvKeterangan.text = context.getString(R.string.jumlah_keranjang, keranjangList.size.toString())
                    }
                }

                if (edit) {
                    cbKeranjang.visibility = View.VISIBLE
                } else {
                    cbKeranjang.visibility = View.GONE
                }

                cbKeranjang.isChecked = pilihSemua
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemKeranjangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return allKeranjangList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(allKeranjangList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: AllKeranjang)
    }
}