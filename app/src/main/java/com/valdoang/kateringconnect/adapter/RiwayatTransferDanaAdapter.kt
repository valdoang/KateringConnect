package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemRiwayatTransferDanaBinding
import com.valdoang.kateringconnect.databinding.ItemTransferDanaBinding
import com.valdoang.kateringconnect.model.TarikDana
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.utils.withTimestampToDateTimeFormat

class RiwayatTransferDanaAdapter(
    private val context: Context
) : RecyclerView.Adapter<RiwayatTransferDanaAdapter.MyViewHolder>() {

    private val riwayatTransferDanaList = ArrayList<TarikDana>()
    private var onItemClickCallback: OnItemClickCallback? = null
    private var db = Firebase.firestore

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<TarikDana>) {
        riwayatTransferDanaList.clear()
        riwayatTransferDanaList.addAll(itemList)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemRiwayatTransferDanaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(riwayatTransferDana: TarikDana) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(riwayatTransferDana)
            }
            binding.apply {
                tvTanggalPengajuan.text = riwayatTransferDana.tanggalPengajuan?.withTimestampToDateTimeFormat()
                tvTanggalDibayarkan.text = riwayatTransferDana.tanggalDibayarkan?.withTimestampToDateTimeFormat()
                tvNominal.text = context.getString(R.string.rupiah_text, riwayatTransferDana.nominal?.withNumberingFormat())
                val userRef = db.collection("user").document(riwayatTransferDana.userId!!)
                userRef.addSnapshotListener { userSnapshot, _ ->
                    if (userSnapshot != null) {
                        val foto = userSnapshot.data?.get("foto").toString()
                        val nama = userSnapshot.data?.get("nama").toString()
                        tvNamaUser.text = nama
                        Glide.with(context).load(foto).error(R.drawable.default_profile).into(ivUser)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemRiwayatTransferDanaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return riwayatTransferDanaList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(riwayatTransferDanaList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: TarikDana)
    }
}