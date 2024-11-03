package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemTarikDanaBinding
import com.valdoang.kateringconnect.model.TarikDana
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.utils.withTimestampToDateTimeFormat

class RiwayatTarikDanaAdapter(
    private val context: Context
) : RecyclerView.Adapter<RiwayatTarikDanaAdapter.MyViewHolder>() {

    private val tarikDanaList = ArrayList<TarikDana>()
    private var onItemClickCallback: OnItemClickCallback? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<TarikDana>) {
        tarikDanaList.clear()
        tarikDanaList.addAll(itemList)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemTarikDanaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tarikDana: TarikDana) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(tarikDana)
            }
            binding.apply {
                tvIdTarikDana.text = tarikDana.id
                tvTanggal.text = tarikDana.tanggalPengajuan?.withTimestampToDateTimeFormat()
                tvNominal.text = context.getString(R.string.rupiah_text, tarikDana.nominal?.withNumberingFormat())

                when (tarikDana.status) {
                    context.getString(R.string.status_proses) -> {
                        tvStatus.text = context.getString(R.string.status_proses)
                        tvStatus.setTextColor(context.resources.getColor(R.color.orange))
                        tvStatus.background = context.resources.getDrawable(R.drawable.status_proses_bg)
                    }
                    context.getString(R.string.status_selesai) -> {
                        tvStatus.text = context.getString(R.string.status_selesai)
                        tvStatus.setTextColor(context.resources.getColor(R.color.green_200))
                        tvStatus.background = context.resources.getDrawable(R.drawable.status_selesai_bg)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemTarikDanaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tarikDanaList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(tarikDanaList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: TarikDana)
    }
}