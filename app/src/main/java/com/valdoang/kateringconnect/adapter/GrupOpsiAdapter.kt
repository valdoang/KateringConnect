package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.databinding.ItemGrupOpsiBinding
import com.valdoang.kateringconnect.model.GrupOpsi
import com.valdoang.kateringconnect.model.Opsi

class GrupOpsiAdapter(
    private val context: Context, private val vendorId: String, private val opsiListCheck: ArrayList<Opsi>,
    private val btnAddKeranjang: Button, private val grupOpsiId: ArrayList<String>,
    private var menuPrice: String, private var totalJumlah: EditText, private var namaOpsi: String
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
        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(grupOpsi: GrupOpsi) {
            binding.apply {
                grupOpsiName.text = grupOpsi.nama

                //Setup View
                val recyclerView: RecyclerView = rvOpsi
                val opsiAdapter = OpsiAdapter(context, opsiListCheck, btnAddKeranjang, grupOpsiId, menuPrice, totalJumlah, ivSuccess, namaOpsi)
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = opsiAdapter

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

                        for (i in opsiList) {
                            if (namaOpsi.contains(i.nama!!)) {
                                i.isChecked = true
                                ivSuccess.visibility = View.VISIBLE
                            }
                        }

                        opsiList.sortBy { opsi ->
                            opsi.harga
                        }

                        opsiList.removeIf { opsi ->
                            opsi.aktif == false
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(grupOpsiList[position])
    }
}