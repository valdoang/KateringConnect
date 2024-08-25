package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemPesananBinding
import com.valdoang.kateringconnect.model.Keranjang
import com.valdoang.kateringconnect.utils.withNumberingFormat


class PesananAdapter(
    private val context: Context, private val vendorId: String
) : RecyclerView.Adapter<PesananAdapter.MyViewHolder>() {

    private val pesananList = ArrayList<Keranjang>()
    private var onItemClickCallback: OnItemClickCallback? = null
    private var db = Firebase.firestore
    private var userId = FirebaseAuth.getInstance().currentUser!!.uid

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Keranjang>) {
        pesananList.clear()
        pesananList.addAll(itemList)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemPesananBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pesanan: Keranjang) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(pesanan)
            }
            binding.apply {
                val kategoriMenuRef = db.collection("user").document(vendorId).collection("kategoriMenu").document(pesanan.kategoriMenuId!!)
                kategoriMenuRef.get().addOnSuccessListener { snapshot ->
                    if (snapshot != null) {
                        val namaKategori = snapshot.data?.get("nama").toString()
                        tvNamaMenu.text = context.getString(R.string.nama_menu_detail_pesanan, pesanan.namaMenu, namaKategori)
                    }
                }
                tvJumlahPesanan.text = context.getString(R.string.tv_jumlah_pesanan, pesanan.jumlah)
                tvNamaOpsi.text = pesanan.namaOpsi
                tvCatatan.text = pesanan.catatan
                tvSubtotal.text = pesanan.subtotal?.withNumberingFormat()

                if (pesanan.catatan == "") {
                    tvCatatan.visibility = View.GONE
                } else {
                    tvCatatan.visibility = View.VISIBLE
                }

                if (pesanan.namaOpsi == "") {
                    tvNamaOpsi.visibility = View.GONE
                } else {
                    tvNamaOpsi.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemPesananBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return pesananList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(pesananList[position])
    }

    fun deleteItem(adapterPosition: Int, vendorId: String) {
        val keranjangId = pesananList[adapterPosition].id
        val keranjangRef = db.collection("user").document(userId).collection("keranjang").document(vendorId)
        keranjangRef.collection("pesanan").get().addOnSuccessListener {
            if (it != null) {
                if (it.size() == 1) {
                    keranjangRef.delete()
                }
            }
        }
        keranjangRef.collection("pesanan").document(keranjangId!!).delete()

        pesananList.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Keranjang)
    }
}