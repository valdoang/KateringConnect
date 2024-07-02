package com.valdoang.kateringconnect.view.user.detailvendor

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.KategoriMenuAdapter
import com.valdoang.kateringconnect.databinding.ActivityDetailVendorBinding
import com.valdoang.kateringconnect.model.KategoriMenu
import com.valdoang.kateringconnect.model.Star
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.roundOffDecimal
import com.valdoang.kateringconnect.utils.withNumberingFormat
import com.valdoang.kateringconnect.view.both.chat.RoomChatActivity
import com.valdoang.kateringconnect.view.both.nilai.NilaiActivity

class DetailVendorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailVendorBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var tvVendorStar: TextView
    private lateinit var tvName: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvNoPhone: TextView
    private lateinit var ivVendorAkun: ImageView
    private lateinit var starList: ArrayList<Star>
    private var foto: String? = null
    private var nama: String? = null
    private var vendorId: String? = null
    private var totalNilai = 0.0
    private lateinit var recyclerView: RecyclerView
    private var kategoriMenuList: ArrayList<KategoriMenu> = ArrayList()
    private lateinit var kategoriMenuAdapter: KategoriMenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailVendorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        vendorId = intent.getStringExtra(Cons.EXTRA_ID)

        firebaseAuth = Firebase.auth
        starList = arrayListOf()

        tvVendorStar = binding.tvVendorStar
        tvName = binding.tvVendorAkunName
        tvAddress = binding.tvAddress
        tvNoPhone = binding.tvNoPhone
        ivVendorAkun = binding.ivVendorAkun

        setupAccount()
        setupView()
        setupMenu()
        setupAction()
    }

    private fun setupAccount() {
        val nilaiRef = db.collection("nilai").whereEqualTo("vendorId", vendorId)
        nilaiRef.addSnapshotListener { snapshot,_ ->
            if (snapshot != null) {
                starList.clear()
                for (data in snapshot.documents) {
                    val star: Star? = data.toObject(Star::class.java)
                    if (star != null) {
                        starList.add(star)
                    }
                }

                for (i in starList) {
                    val nilai = i.nilai?.toDouble()
                    if (nilai != null) {
                        totalNilai += nilai
                    }
                }

                val sizeNilai = starList.size
                val nilaiStar = totalNilai/sizeNilai

                if (sizeNilai == 0) {
                    tvVendorStar.text = getString(R.string.tidak_ada_penilaian)
                }
                else {
                    tvVendorStar.text = getString(R.string.vendor_star, nilaiStar.roundOffDecimal(), sizeNilai.toString().withNumberingFormat())
                }

            }
        }

        val vendorRef = db.collection("user").document(vendorId!!)
        vendorRef.addSnapshotListener { document,_ ->
            if (document != null) {
                foto = document.data?.get("foto").toString()
                nama = document.data?.get("nama").toString()
                val kota = document.data?.get("kota").toString()
                val alamat = document.data?.get("alamat").toString()
                val telepon = document.data?.get("telepon").toString()

                Glide.with(applicationContext).load(foto).error(R.drawable.default_vendor_profile).into(ivVendorAkun)
                tvName.text = nama
                tvAddress.text = getString(R.string.tv_address_city, alamat, kota)
                tvNoPhone.text = telepon
            }
        }
    }

    private fun setupMenu() {
        val kategoriMenuRef = db.collection("user").document(vendorId!!).collection("kategoriMenu")
        kategoriMenuRef.get().addOnSuccessListener { snapshot ->
            if (snapshot != null) {
                kategoriMenuList.clear()
                for (data in snapshot.documents) {
                    val kategoriMenu: KategoriMenu? = data.toObject(KategoriMenu::class.java)
                    if (kategoriMenu != null) {
                        val menuRef = kategoriMenuRef.document(data.id).collection("menu")
                        menuRef.get().addOnSuccessListener {
                            val menuSize = it.size()
                            if (menuSize > 0) {
                                kategoriMenu.id = data.id
                                kategoriMenuList.add(kategoriMenu)
                                kategoriMenuList.sortBy { kategori ->
                                    kategori.nama
                                }
                                kategoriMenuAdapter.setItems(kategoriMenuList)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupView() {
        recyclerView = binding.rvMenu
        recyclerView.layoutManager = LinearLayoutManager(this)
        kategoriMenuAdapter = KategoriMenuAdapter(this, vendorId!!)
        recyclerView.adapter = kategoriMenuAdapter
    }
    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }

        binding.clGaleri.setOnClickListener {
            val intent = Intent(this, GaleriVendorActivity::class.java)
            intent.putExtra(Cons.EXTRA_ID, vendorId)
            startActivity(intent)
        }

        binding.clStar.setOnClickListener {
            val intent = Intent(this, NilaiActivity::class.java)
            intent.putExtra(Cons.EXTRA_ID, vendorId)
            startActivity(intent)
        }
        binding.ibChat.setOnClickListener {
            val intent = Intent(this, RoomChatActivity::class.java)
            intent.putExtra(Cons.EXTRA_ID, vendorId)
            startActivity(intent)
        }
    }
}