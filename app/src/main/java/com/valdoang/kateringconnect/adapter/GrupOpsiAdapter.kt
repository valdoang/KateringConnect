package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemGrupOpsiBinding
import com.valdoang.kateringconnect.model.GrupOpsi
import com.valdoang.kateringconnect.model.Opsi
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.view.vendor.menu.grupopsi.EditGrupOpsiActivity

class GrupOpsiAdapter(
    private val context: Context
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
                tvNamaGrupOpsi.text = grupOpsi.nama
                tvJumlahTersambung.text = context.getString(
                    R.string.jumlah_hidangan_tersambung,
                    grupOpsi.menuId?.size ?: "0"
                )

                ivExpandMore.setOnClickListener {
                    rvOpsiAktif.visibility = View.VISIBLE
                    ivExpandMore.visibility = View.GONE
                    ivExpandLess.visibility = View.VISIBLE
                }

                ivExpandLess.setOnClickListener {
                    rvOpsiAktif.visibility = View.GONE
                    ivExpandMore.visibility = View.VISIBLE
                    ivExpandLess.visibility = View.GONE
                }

                //Setup View
                val recyclerView: RecyclerView = rvOpsiAktif
                val opsiAktifAdapter = OpsiAktifAdapter(grupOpsi.id!!)
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = opsiAktifAdapter
                opsiAktifAdapter.setItems(opsiList)

                //Setup Data
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                val ref = db.collection("user").document(userId).collection("grupOpsi")
                    .document(grupOpsi.id!!).collection("opsi")
                ref.addSnapshotListener { snapshot, _ ->
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

                        opsiAktifAdapter.setItems(opsiList)
                    }
                }

                tvEditGrupOpsi.setOnClickListener {
                    //Intent ke EditGrupOpsiActivity
                    val intent = Intent(context, EditGrupOpsiActivity::class.java)
                    intent.putExtra(Cons.EXTRA_ID, grupOpsi.id)
                    context.startActivity(intent)
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