package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.databinding.ItemAcGrupOpsiBinding
import com.valdoang.kateringconnect.model.GrupOpsi

class AcGrupOpsiAdapter(
    private var arrayGrupOpsiId: ArrayList<String>
    ) : RecyclerView.Adapter<AcGrupOpsiAdapter.MyViewHolder>() {

    private val acGrupOpsiList = ArrayList<GrupOpsi>()
    private var db = Firebase.firestore

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<GrupOpsi>) {
        acGrupOpsiList.clear()
        acGrupOpsiList.addAll(itemList)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemAcGrupOpsiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(grupOpsi: GrupOpsi) {
            binding.apply {
                tvNamaGrupOpsi.text = grupOpsi.nama

                //Setup Data
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                val ref = db.collection("user").document(userId).collection("grupOpsi")
                    .document(grupOpsi.id!!).collection("opsi")
                ref.addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        val namaOpsi: ArrayList<String> = arrayListOf()
                        for (data in snapshot.documents) {
                            val nama = data.data?.get("nama").toString()
                            namaOpsi.add(nama)
                        }
                        tvNamaOpsi.text = namaOpsi.joinToString { it }
                    }
                }

                //Check Listener
                if (arrayGrupOpsiId.contains(grupOpsi.id)) {
                    checkBox.isChecked = true
                }
                val checkListener =
                    CompoundButton.OnCheckedChangeListener { _, isChecked ->
                        val grupOpsiId = grupOpsi.id
                        if (isChecked) {
                            if (!arrayGrupOpsiId.contains(grupOpsiId)){
                                if (grupOpsiId != null) {
                                    arrayGrupOpsiId.add(grupOpsiId)
                                }
                            }
                        } else {
                            if (arrayGrupOpsiId.contains(grupOpsiId)){
                                if (grupOpsiId != null) {
                                    arrayGrupOpsiId.remove(grupOpsiId)
                                }
                            }
                        }
                    }

                checkBox.setOnCheckedChangeListener(checkListener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemAcGrupOpsiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return acGrupOpsiList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(acGrupOpsiList[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}