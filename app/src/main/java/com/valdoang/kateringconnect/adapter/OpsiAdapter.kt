package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemOpsiBinding
import com.valdoang.kateringconnect.model.Opsi
import com.valdoang.kateringconnect.utils.allChangedListener
import com.valdoang.kateringconnect.utils.withNumberingFormat


class OpsiAdapter(
    private val context: Context, private val opsiListCheck: ArrayList<Opsi>,
    private val btnPesan: Button, private val grupOpsiId: ArrayList<String>,
    private val menuPrice: String, private val etJumlah: EditText, private val ivSuccess: ImageView
) : RecyclerView.Adapter<OpsiAdapter.MyViewHolder>() {

    private val opsiList = ArrayList<Opsi>()
    private var selectedPosition = -1
    private var subtotal = menuPrice.toLong()
    private var total = 0L
    private var jumlahTotal = etJumlah.text.toString().toLong()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Opsi>) {
        opsiList.clear()
        opsiList.addAll(itemList)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemOpsiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(opsi: Opsi) {
            binding.apply {
                rbNamaGrupOpsi.text = opsi.nama

                val basicPrice = menuPrice.toLong() * jumlahTotal
                btnPesan.text = context.getString(R.string.btn_pesan_menu, basicPrice.withNumberingFormat())

                if (opsi.harga == "0") {
                    tvPrice.visibility = View.GONE
                } else {
                    tvPrice.text = context.getString(R.string.opsi_price, opsi.harga?.withNumberingFormat())
                }

                if (position != selectedPosition) {
                    rbNamaGrupOpsi.isChecked = false
                    opsiListCheck.remove(opsi)
                    subtotal = menuPrice.toLong()
                } else {
                    rbNamaGrupOpsi.isChecked = true
                    opsiListCheck.add(opsi)
                    subtotal = menuPrice.toLong()
                }

                for (i in opsiListCheck) {
                    subtotal += i.harga!!.toLong()
                    total = subtotal * jumlahTotal
                    btnPesan.text = context.getString(R.string.btn_pesan_menu, total.withNumberingFormat())
                }

                etJumlah.allChangedListener {
                    jumlahTotal = it.toLong()
                    subtotal = menuPrice.toLong()
                    btnPesan.text = context.getString(R.string.btn_pesan_menu, total.withNumberingFormat())
                    if (opsiListCheck.size <= 0) {
                        total = subtotal * jumlahTotal
                        btnPesan.text = context.getString(R.string.btn_pesan_menu, total.withNumberingFormat())
                    } else {
                        for (i in opsiListCheck) {
                            subtotal += i.harga!!.toLong()
                            total = subtotal * jumlahTotal
                            btnPesan.text = context.getString(R.string.btn_pesan_menu, total.withNumberingFormat())
                        }
                    }
                }

                btnPesan.isEnabled = grupOpsiId.size == opsiListCheck.size

                val checkListener =
                    CompoundButton.OnCheckedChangeListener { _, isChecked ->
                       if (isChecked) {
                           ivSuccess.visibility = View.VISIBLE
                           selectedPosition = adapterPosition
                           notifyDataSetChanged()
                       }
                    }

                rbNamaGrupOpsi.setOnCheckedChangeListener(checkListener)

                if(position == opsiList.size -1){
                    view.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            ItemOpsiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return opsiList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(opsiList[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}