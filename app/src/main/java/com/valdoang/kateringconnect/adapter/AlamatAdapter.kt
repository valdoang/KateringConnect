package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemAlamatBinding
import com.valdoang.kateringconnect.model.Alamat
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.view.both.editakun.EditAkunActivity
import com.valdoang.kateringconnect.view.user.alamat.edit.EditAlamatActivity


class AlamatAdapter(
    private val context: Context
) : RecyclerView.Adapter<AlamatAdapter.MyViewHolder>() {

    private val alamatList = ArrayList<Alamat>()
    private var onItemClickCallback: OnItemClickCallback? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Alamat>) {
        alamatList.clear()
        alamatList.addAll(itemList)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemAlamatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(alamat: Alamat) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(alamat)
            }
            binding.apply {
                if (alamat.nama != context.getString(R.string.rumah)) {
                    ivAlamat.setImageResource(R.drawable.alamat_tersimpan_icon)
                    ivAlamat.setColorFilter(ContextCompat.getColor(context, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY)
                }
                tvNama.text = alamat.nama
                tvAlamat.text = context.getString(R.string.tv_address_city, alamat.alamat, alamat.kota)
                tvNamaNomorKontak.text = context.getString(R.string.nama_nomor_kontak, alamat.namaKontak, alamat.nomorKontak)
                tvEdit.setOnClickListener {
                    if (alamat.nama == context.getString(R.string.rumah)) {
                        val intent = Intent(context, EditAkunActivity::class.java)
                        intent.putExtra(Cons.EXTRA_ID, alamat.id)
                        context.startActivity(intent)
                    } else {
                        val intent = Intent(context, EditAlamatActivity::class.java)
                        intent.putExtra(Cons.EXTRA_ID, alamat.id)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemAlamatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return alamatList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(alamatList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Alamat)
    }
}