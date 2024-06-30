package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.databinding.ItemGrupOpsiBinding
import com.valdoang.kateringconnect.model.GrupOpsi
import com.valdoang.kateringconnect.model.Opsi

class GrupOpsiAdapter(
    private val context: Context, private val vendorId: String, private val opsiListCheck: ArrayList<Opsi>,
    private val btnPesan: Button, private val grupOpsiId: ArrayList<String>,
    private var menuPrice: String, private var totalJumlah: EditText
) : RecyclerView.Adapter<GrupOpsiAdapter.MyViewHolder>() {

    private val grupOpsiList = ArrayList<GrupOpsi>()
    private var opsiList = ArrayList<Opsi>()
    private var db = Firebase.firestore

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<GrupOpsi>) {
        grupOpsiList.clear()
        grupOpsiList.addAll(itemList)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemGrupOpsiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(grupOpsi: GrupOpsi) {
            binding.apply {
                grupOpsiName.text = grupOpsi.nama

                //Setup View
                val recyclerView: RecyclerView = rvOpsi
                val opsiAdapter = OpsiAdapter(context, opsiListCheck, btnPesan, grupOpsiId, menuPrice, totalJumlah)
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = opsiAdapter
                opsiAdapter.setItems(opsiList)

                //Setup Data
                val ref = db.collection("user").document(vendorId).collection("grupOpsi")
                    .document(grupOpsi.id!!).collection("opsi")
                ref.get().addOnSuccessListener { snapshot ->
                    if (snapshot != null) {
                        opsiList.clear()
                        for (data in snapshot.documents) {
                            val opsi: Opsi? = data.toObject(Opsi::class.java)
                            if (opsi != null) {
                                opsi.id = data.id
                                opsiList.add(opsi)
                            }
                        }

                        opsiList.sortBy { opsi ->
                            opsi.harga
                        }

                        opsiAdapter.setItems(opsiList)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            ItemGrupOpsiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return grupOpsiList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(grupOpsiList[position])
    }
}