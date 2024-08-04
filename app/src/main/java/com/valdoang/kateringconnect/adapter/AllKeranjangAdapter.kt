package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
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
    private val context: Context, private val edit: Boolean,
    private val arrayVendorId: ArrayList<String>,
    private val btnHapus: Button, private val cbPilihSemua: CheckBox
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
                val vendorId = allKeranjang.vendorId
                val vendorRef = db.collection("user").document(vendorId!!)
                vendorRef.get().addOnSuccessListener { vendorSnapshot ->
                    if (vendorSnapshot != null) {
                        val namaVendor = vendorSnapshot.data?.get("nama").toString()
                        tvNamaVendor.text = namaVendor
                    }
                }

                val keranjangRef = db.collection("user").document(userId).collection("keranjang").document(vendorId).collection("pesanan")
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
                        var jumlahPesanan = 0
                        for (i in keranjangList) {
                            jumlahPesanan += i.jumlah!!.toInt()
                        }
                        tvKeterangan.text = context.getString(R.string.jumlah_keranjang, jumlahPesanan.toString(), allKeranjang.jarak)
                    }
                }

                if (edit) {
                    cbKeranjang.visibility = View.VISIBLE
                } else {
                    cbKeranjang.visibility = View.GONE
                    arrayVendorId.clear()
                    cbPilihSemua.isChecked = false
                    btnHapus.text = context.getString(R.string.hapus)
                }

                cbKeranjang.isChecked = arrayVendorId.contains(vendorId)

                val checkListener =
                    CompoundButton.OnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            if (!arrayVendorId.contains(vendorId)){
                                arrayVendorId.add(vendorId)
                            }
                        } else {
                            if (arrayVendorId.contains(vendorId)){
                                arrayVendorId.remove(vendorId)
                            }
                        }

                        if (arrayVendorId.size == 0) {
                            btnHapus.text = context.getString(R.string.hapus)
                            btnHapus.isEnabled = false
                        } else {
                            btnHapus.text = context.getString(R.string.hapus_jumlah_keranjang, arrayVendorId.size.toString())
                            btnHapus.isEnabled = true
                        }

                        cbPilihSemua.isChecked = allKeranjangList.size == arrayVendorId.size
                    }

                cbKeranjang.setOnCheckedChangeListener(checkListener)
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