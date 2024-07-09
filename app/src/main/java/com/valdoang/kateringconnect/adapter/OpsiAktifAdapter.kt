package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.databinding.ItemOpsiAktifBinding
import com.valdoang.kateringconnect.model.Opsi
import com.valdoang.kateringconnect.utils.withCurrencyFormat


class OpsiAktifAdapter(
    private val grupOpsiId: String
) : RecyclerView.Adapter<OpsiAktifAdapter.MyViewHolder>() {

    private val opsiList = ArrayList<Opsi>()
    private var db = Firebase.firestore

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<Opsi>) {
        opsiList.clear()
        opsiList.addAll(itemList)
        notifyDataSetChanged()
    }


    inner class MyViewHolder(private val binding: ItemOpsiAktifBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(opsi: Opsi) {
            binding.apply {
                tvGrupOpsiName.text = opsi.nama
                tvGrupOpsiPrice.text = opsi.harga?.withCurrencyFormat()
                switchButton.isChecked = opsi.aktif == true

                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                val ref = db.collection("user").document(userId).collection("grupOpsi")
                    .document(grupOpsiId).collection("opsi").document(opsi.id!!)

                val checkListener =
                    CompoundButton.OnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            if (opsi.aktif == false) {
                                ref.update("aktif", true)
                            }
                        } else {
                            if (opsi.aktif == true) {
                                ref.update("aktif", false)
                            }
                        }
                    }

                switchButton.setOnCheckedChangeListener(checkListener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            ItemOpsiAktifBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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