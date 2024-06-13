package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION_CODES.M
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemKategoriMenuBinding
import com.valdoang.kateringconnect.model.AcKategori
import com.valdoang.kateringconnect.model.KategoriMenu
import com.valdoang.kateringconnect.model.Menu

class KategoriMenuAdapter(
    private val context: Context
): RecyclerView.Adapter<KategoriMenuAdapter.MyViewHolder>() {

    private val kategoriMenuList = ArrayList<KategoriMenu>()
    private var menuList = ArrayList<Menu>()
    private var db = Firebase.firestore

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(itemList: List<KategoriMenu>) {
        kategoriMenuList.clear()
        kategoriMenuList.addAll(itemList)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemKategoriMenuBinding) :
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
                val menuAdapter = MenuAdapter(context)
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = menuAdapter
                menuAdapter.setItems(menuList)

                //Setup Data
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                val ref = db.collection("user").document(userId).collection("kategoriMenu").document(kategoriMenu.id!!).collection("menu")
                ref.addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        menuList.clear()
                        for (data in snapshot.documents) {
                            val menu: Menu? = data.toObject(Menu::class.java)
                            if (menu != null) {
                                menu.id = data.id
                                menuList.add(menu)
                            }
                        }

                        menuAdapter.setItems(menuList)
                    }
                }

                tvEditKategori.setOnClickListener {
                    //Intent ke EditKategoriActivity
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemKategoriMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return kategoriMenuList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(kategoriMenuList[position])
    }
}