package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemPesananBinding
import com.valdoang.kateringconnect.model.Keranjang
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.view.user.tambahpesanan.TambahPorsiFragment

class DetailPesananPemesananAdapter(
    private val context: Context, private val user: String, private val status: String,
    private val mTotalPembayaranValue: TextView, private val mSubtotal: TextView,
    private val mSubtotalValue: TextView, ongkir: Long, private val vendorId: String,
    private val pesananId: String
) : RecyclerView.Adapter<DetailPesananPemesananAdapter.MyViewHolder>() {

    private val menuPesananList = ArrayList<Keranjang>()
    var jumlahPesanan = 0
    var tSubtotal = 0L
    var total = ongkir
    private var db = Firebase.firestore

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Keranjang>) {
        menuPesananList.clear()
        menuPesananList.addAll(itemList)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemPesananBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menuPesanan: Keranjang) {
            binding.apply {
                val kategoriMenuRef = db.collection("user").document(vendorId).collection("kategoriMenu").document(menuPesanan.kategoriMenuId!!)
                kategoriMenuRef.get().addOnSuccessListener { snapshot ->
                    if (snapshot != null) {
                        val namaKategori = snapshot.data?.get("nama").toString()
                        tvNamaMenu.text = context.getString(R.string.nama_menu_detail_pesanan, menuPesanan.namaMenu, namaKategori)
                    }
                }
                tvJumlahPesanan.text = context.getString(R.string.tv_jumlah_pesanan, menuPesanan.jumlah)
                tvNamaOpsi.text = menuPesanan.namaOpsi
                tvCatatan.text = menuPesanan.catatan
                tvSubtotal.text = menuPesanan.subtotal?.withNumberingFormat()
                tvEdit.text = context.getString(R.string.tambah_porsi)

                jumlahPesanan += menuPesanan.jumlah!!.toInt()
                tSubtotal += menuPesanan.subtotal!!.toLong()
                total += menuPesanan.subtotal!!.toLong()

                mSubtotal.text = context.getString(R.string.subtotal_jumlah, jumlahPesanan.toString())
                mSubtotalValue.text = tSubtotal.withNumberingFormat()
                mTotalPembayaranValue.text = total.withNumberingFormat()

                if (menuPesanan.catatan == "") {
                    tvCatatan.visibility = View.GONE
                } else {
                    tvCatatan.visibility = View.VISIBLE
                }

                if (menuPesanan.namaOpsi == "") {
                    tvNamaOpsi.visibility = View.GONE
                } else {
                    tvNamaOpsi.visibility = View.VISIBLE
                }

                if (menuPesanan.tambahPorsi == true) {
                    ivBadge.visibility = View.VISIBLE
                    tvPorsiAdd.visibility = View.VISIBLE
                } else {
                    ivBadge.visibility = View.GONE
                    tvPorsiAdd.visibility = View.GONE
                }

                when (user) {
                    context.getString(R.string.pembeli) -> {
                        tvEdit.visibility = View.VISIBLE
                        tvPorsiAdd.text = context.getString(R.string.porsi_added_user)
                    }
                    context.getString(R.string.vendor) -> {
                        tvEdit.visibility = View.GONE
                        tvPorsiAdd.text = context.getString(R.string.porsi_added_vendor)
                    }
                }

                if (status == context.getString(R.string.status_selesai) ||  status == context.getString(R.string.status_batal)) {
                    tvEdit.visibility = View.GONE
                    ivBadge.visibility = View.GONE
                    tvPorsiAdd.visibility = View.GONE
                }

                tvEdit.setOnClickListener {
                    val args = Bundle()
                    args.putString("pesananId", pesananId)
                    args.putString("menuPesananId", menuPesanan.id)
                    val dialog: DialogFragment = TambahPorsiFragment()
                    dialog.arguments = args
                    dialog.show((context as AppCompatActivity).supportFragmentManager, "tambahPorsiDialog")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            ItemPesananBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return menuPesananList.size
    }
    
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(menuPesananList[position])
    }
}