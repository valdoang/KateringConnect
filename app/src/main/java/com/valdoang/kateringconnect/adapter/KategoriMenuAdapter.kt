package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemKategoriMenuBinding
import com.valdoang.kateringconnect.model.KategoriMenu
import com.valdoang.kateringconnect.model.Keranjang
import com.valdoang.kateringconnect.model.Menu
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.view.user.custommenu.CustomMenuActivity

class KategoriMenuAdapter(
    private val context: Context, private val vendorId: String,
    private val alamatId: String, private val ongkir: String,
    private val btnCheckout: Button
): RecyclerView.Adapter<KategoriMenuAdapter.MyViewHolder>() {

    private val kategoriMenuList = ArrayList<KategoriMenu>()
    private var menuList = ArrayList<Menu>()
    private var keranjangList: ArrayList<Keranjang> = ArrayList()
    private var db = Firebase.firestore
    private var userId = FirebaseAuth.getInstance().currentUser!!.uid

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(kategoriMenu: List<KategoriMenu>) {
        kategoriMenuList.clear()
        kategoriMenuList.addAll(kategoriMenu)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemKategoriMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(kategoriMenu: KategoriMenu) {
            binding.apply {
                kategoriName.text = kategoriMenu.nama

                //Setup View
                val recyclerView: RecyclerView = rvMenu
                val menuAdapter = MenuAdapter(context, btnCheckout, vendorId)
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = menuAdapter
                menuAdapter.setItems(menuList)

                //Setup Data
                val ref = db.collection("user").document(vendorId).collection("kategoriMenu").document(kategoriMenu.id!!).collection("menu")
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

                        menuList.sortBy { menu ->
                            menu.nama
                        }

                        menuAdapter.setItems(menuList)
                        menuAdapter.setOnItemClickCallback(object :
                            MenuAdapter.OnItemClickCallback {
                            @RequiresApi(Build.VERSION_CODES.N)
                            override fun onItemClicked(data: Menu) {
                                val keranjangRef = db.collection("user").document(userId).collection("keranjang").document(vendorId).collection("pesanan").whereEqualTo("menuId", data.id)
                                keranjangRef.addSnapshotListener { keranjangSnapshot, _ ->
                                    if (keranjangSnapshot != null) {
                                        keranjangList.clear()
                                        for (dataKeranjang in keranjangSnapshot.documents) {
                                            val keranjang: Keranjang? =
                                                dataKeranjang.toObject(Keranjang::class.java)
                                            if (keranjang != null) {
                                                keranjang.id = dataKeranjang.id
                                                keranjangList.add(keranjang)
                                            }
                                        }
                                    }

                                    if (data.aktif == true) {
                                        if (data.grupOpsiId!!.isNotEmpty() && keranjangList.isNotEmpty()) {
                                            val dialog = BottomSheetDialog(context)
                                            val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_add_or_edit_keranjang, null)

                                            val titleNamaMenu = view.findViewById<TextView>(R.id.title_nama_menu)
                                            val titleHargaMenu = view.findViewById<TextView>(R.id.title_harga_menu)
                                            val rvKeranjang = view.findViewById<RecyclerView>(R.id.rv_keranjang)
                                            val btnSatuLagi = view.findViewById<Button>(R.id.btn_satu_lagi)

                                            titleNamaMenu.text = data.nama
                                            titleHargaMenu.text = data.harga?.withNumberingFormat()
                                            btnSatuLagi.setOnClickListener {
                                                val intent = Intent(context, CustomMenuActivity::class.java)
                                                intent.putExtra(Cons.EXTRA_ID, vendorId)
                                                intent.putExtra(Cons.EXTRA_SEC_ID, kategoriMenu.id)
                                                intent.putExtra(Cons.EXTRA_THIRD_ID, data.id)
                                                intent.putExtra(Cons.EXTRA_FOURTH_ID, alamatId)
                                                intent.putExtra(Cons.EXTRA_ONGKIR, ongkir)
                                                context.startActivity(intent)
                                            }

                                            //Setup View
                                            val recyclerViewKeranjang: RecyclerView = rvKeranjang
                                            val addOrEditKeranjangAdapter = AddOrEditKeranjangAdapter(context)
                                            recyclerViewKeranjang.layoutManager = LinearLayoutManager(context)
                                            recyclerViewKeranjang.adapter = addOrEditKeranjangAdapter
                                            addOrEditKeranjangAdapter.setItems(keranjangList)

                                            //Setup Data
                                            addOrEditKeranjangAdapter.setItems(keranjangList)
                                            addOrEditKeranjangAdapter.setOnItemClickCallback(object :
                                                AddOrEditKeranjangAdapter.OnItemClickCallback {
                                                override fun onItemClicked(keranjang: Keranjang) {
                                                    val intent = Intent(context, CustomMenuActivity::class.java)
                                                    intent.putExtra(Cons.EXTRA_ID, vendorId)
                                                    intent.putExtra(Cons.EXTRA_SEC_ID, kategoriMenu.id)
                                                    intent.putExtra(Cons.EXTRA_THIRD_ID, data.id)
                                                    intent.putExtra(Cons.EXTRA_FOURTH_ID, alamatId)
                                                    intent.putExtra(Cons.EXTRA_FIFTH_ID, keranjang.id)
                                                    intent.putExtra(Cons.EXTRA_ONGKIR, ongkir)
                                                    context.startActivity(intent)
                                                }
                                            })

                                            dialog.setContentView(view)
                                            dialog.show()
                                        } else {
                                            val intent = Intent(context, CustomMenuActivity::class.java)
                                            intent.putExtra(Cons.EXTRA_ID, vendorId)
                                            intent.putExtra(Cons.EXTRA_SEC_ID, kategoriMenu.id)
                                            intent.putExtra(Cons.EXTRA_THIRD_ID, data.id)
                                            intent.putExtra(Cons.EXTRA_FOURTH_ID, alamatId)
                                            intent.putExtra(Cons.EXTRA_ONGKIR, ongkir)
                                            context.startActivity(intent)
                                        }
                                    }
                                }
                            }
                        })
                    }
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