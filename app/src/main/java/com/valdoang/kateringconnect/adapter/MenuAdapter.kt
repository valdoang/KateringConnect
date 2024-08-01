package com.valdoang.kateringconnect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.databinding.ItemMenuBinding
import com.valdoang.kateringconnect.model.Keranjang
import com.valdoang.kateringconnect.model.Menu
import com.valdoang.kateringconnect.utils.withNumberingFormat


class MenuAdapter(
    private val context: Context, private val btnCheckout: Button,
    private val vendorId: String
) : RecyclerView.Adapter<MenuAdapter.MyViewHolder>() {

    private val menuList = ArrayList<Menu>()
    private var db = Firebase.firestore
    private var userId = FirebaseAuth.getInstance().currentUser!!.uid
    private var keranjangList: ArrayList<Keranjang> = ArrayList()
    private var onItemClickCallback: OnItemClickCallback? = null
    private var jumlahPesanan = 0
    private var total = 0L

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(menu: List<Menu>) {
        menuList.clear()
        menuList.addAll(menu)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class MyViewHolder(private val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: Menu) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(menu)
            }
            binding.apply {
                if (menu.aktif == false) {
                    val matrix = ColorMatrix()
                    matrix.setSaturation(0f)
                    ivMenu.colorFilter = ColorMatrixColorFilter(matrix)
                    tvTidakTersedia.visibility = View.VISIBLE
                    tvMenuName.setTextColor(context.resources.getColor(R.color.grey))
                    tvMenuDesc.visibility =View.GONE
                    tvMenuPrice.setTextColor(context.resources.getColor(R.color.grey))
                }
                Glide.with(context).load(menu.foto).into(ivMenu)
                tvMenuName.text = menu.nama
                tvMenuDesc.text = menu.keterangan
                tvMenuPrice.text = menu.harga?.withNumberingFormat()

                val keranjangRef = db.collection("user").document(userId).collection("keranjang").document(vendorId).collection("pesanan").whereEqualTo("menuId", menu.id)
                keranjangRef.addSnapshotListener { keranjangSnapshot, _ ->
                    jumlahPesanan = 0
                    total = 0L
                    if (keranjangSnapshot != null) {
                        keranjangList.clear()
                        for (data in keranjangSnapshot.documents) {
                            val keranjang: Keranjang? = data.toObject(Keranjang::class.java)
                            if (keranjang != null) {
                                keranjang.id = data.id
                                keranjangList.add(keranjang)
                            }
                        }

                        var totalJumlah = 0
                        var subtotal = 0L

                        for (i in keranjangList) {
                            totalJumlah += i.jumlah!!.toInt()
                            subtotal += i.subtotal!!.toLong()
                        }

                        //TODO: KEMUNGKINAN KETIKA ADA KERANJANG BARU JUMLAH PESANAN DAN TOTAL AKAN ERROR, KARENA TIDAK MULAI DARI 0
                        jumlahPesanan += totalJumlah
                        total += subtotal

                        if (keranjangList.isNotEmpty()) {
                            cvJumlah.visibility = View.VISIBLE
                            tvJumlah.text = totalJumlah.toString()
                            btnCheckout.text = context.getString(
                                R.string.btn_checkout_keranjang,
                                jumlahPesanan.toString(),
                                total.withNumberingFormat()
                            )
                        } else {
                            cvJumlah.visibility = View.GONE
                            btnCheckout.text = context.getString(
                                R.string.btn_checkout_keranjang,
                                jumlahPesanan.toString(),
                                total.withNumberingFormat()
                            )
                        }


                    }
                }

                if(position == menuList.size -1){
                    view.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(menuList[position])
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Menu)
    }
}