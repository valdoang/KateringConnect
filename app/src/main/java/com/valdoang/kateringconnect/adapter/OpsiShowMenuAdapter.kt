package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemOpsiShowMenuBinding
import com.valdoang.kateringconnect.model.KategoriMenu
import com.valdoang.kateringconnect.model.Menu

class OpsiShowMenuAdapter(
    private val context: Context, private var arrayMenuId: ArrayList<String>, private var grupOpsiId: String, private var btnSimpan: Button
): RecyclerView.Adapter<OpsiShowMenuAdapter.MyViewHolder>() {

    private val kategoriMenuList = ArrayList<KategoriMenu>()
    private var menuList = ArrayList<Menu>()
    private var db = Firebase.firestore
    private var checkAccumulator = 0

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<KategoriMenu>) {
        kategoriMenuList.clear()
        kategoriMenuList.addAll(itemList)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemOpsiShowMenuBinding, private val lifecycleOwner: LifecycleOwner) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(kategoriMenu: KategoriMenu) {
            binding.apply {
                tvNamaKategori.text = kategoriMenu.nama

                ivExpandMore.setOnClickListener {
                    rvMenu.visibility = View.VISIBLE
                    ivExpandMore.visibility = View.GONE
                    ivExpandLess.visibility = View.VISIBLE
                }

                ivExpandLess.setOnClickListener {
                    rvMenu.visibility = View.GONE
                    ivExpandMore.visibility = View.VISIBLE
                    ivExpandLess.visibility = View.GONE
                }

                //Setup View
                val recyclerView: RecyclerView = rvMenu
                val opsiChooseMenuAdapter = OpsiChooseMenuAdapter(context, arrayMenuId, grupOpsiId, btnSimpan)
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = opsiChooseMenuAdapter
                opsiChooseMenuAdapter.setItems(menuList)

                //Setup Data
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                val ref = db.collection("user").document(userId).collection("kategoriMenu").document(kategoriMenu.id!!).collection("menu")
                ref.addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        checkAccumulator = 0
                        menuList.clear()
                        for (data in snapshot.documents) {
                            val menu: Menu? = data.toObject(Menu::class.java)
                            if (menu != null) {
                                menu.id = data.id
                                if (arrayMenuId.contains(menu.id)) {
                                    checkAccumulator = checkAccumulator.plus(1)
                                    tvJumlahTersambung.text = context.getString(R.string.jumlah_hidangan_tersambung, checkAccumulator.toString())
                                }
                                menuList.add(menu)
                            }
                        }

                        opsiChooseMenuAdapter.setItems(menuList)
                    }
                }

                opsiChooseMenuAdapter.checkAccumulator.observe(lifecycleOwner) {
                    tvJumlahTersambung.text = context.getString(R.string.jumlah_hidangan_tersambung, it.toString())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val lifeCycleOwner = parent.context as LifecycleOwner
        val itemView = ItemOpsiShowMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView, lifeCycleOwner)
    }

    override fun getItemCount(): Int {
        return kategoriMenuList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(kategoriMenuList[position])
    }
}