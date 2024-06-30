package com.valdoang.kateringconnect.view.user.detailvendor

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.GrupOpsiAdapter
import com.valdoang.kateringconnect.databinding.ActivityCustomMenuBinding
import com.valdoang.kateringconnect.model.GrupOpsi
import com.valdoang.kateringconnect.model.Opsi
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.withNumberingFormat

class CustomMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomMenuBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var ivMenu: ImageView
    private lateinit var tvMenuName: TextView
    private lateinit var tvMenuDesc: TextView
    private lateinit var tvMenuPrice: TextView
    private lateinit var tvHargaDasar: TextView
    private lateinit var btnPesan: Button
    private lateinit var ibLess: ImageButton
    private lateinit var ibMore: ImageButton
    private lateinit var etJumlah: EditText
    private var vendorId: String? = null
    private var kategoriId: String? = null
    private var menuId: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var grupOpsiAdapter: GrupOpsiAdapter
    private var grupOpsiId: ArrayList<String>? = ArrayList()
    private var menuPrice = ""
    private var grupOpsiList: ArrayList<GrupOpsi> = ArrayList()
    private var opsiListCheck: ArrayList<Opsi> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        //TODO: OPSIONAL UI. MENUMPUK IV MENU BERADA PADA STATUS BAR

        firebaseAuth = Firebase.auth

        vendorId = intent.getStringExtra(Cons.EXTRA_ID)
        kategoriId = intent.getStringExtra(Cons.EXTRA_SEC_ID)
        menuId = intent.getStringExtra(Cons.EXTRA_THIRD_ID)

        ivMenu = binding.ivMenu
        tvMenuName = binding.tvMenuName
        tvMenuDesc = binding.tvMenuDesc
        tvMenuPrice = binding.tvMenuPrice
        tvHargaDasar = binding.tvHargaDasar
        btnPesan = binding.btnPesan
        ibLess = binding.ibLess
        ibMore = binding.ibMore
        etJumlah = binding.etJumlah

        setupAction()
        setupView()
        setupData()
        editJumlah()
        pesan()
    }

    private fun setupData() {
        val menuRef = db.collection("user").document(vendorId!!).collection("kategoriMenu").document(kategoriId!!).collection("menu").document(menuId!!)
        menuRef.get().addOnSuccessListener {  menuSnapshot ->
            if (menuSnapshot != null) {
                val menuImage = menuSnapshot.data?.get("foto").toString()
                val menuName = menuSnapshot.data?.get("nama").toString()
                val menuDesc = menuSnapshot.data?.get("keterangan").toString()
                menuPrice = menuSnapshot.data?.get("harga").toString()
                grupOpsiId = menuSnapshot.data?.get("grupOpsiId") as? ArrayList<String>
                setupView()

                Glide.with(this).load(menuImage).error(R.drawable.default_menu).into(ivMenu)
                tvMenuName.text = menuName
                tvMenuDesc.text = menuDesc
                tvMenuPrice.text = menuPrice.withNumberingFormat()
                if (grupOpsiId == null) {
                    binding.clGrupOpsi.visibility = View.GONE
                } else {
                    for (i in grupOpsiId!!) {
                        val grupOpsiRef = db.collection("user").document(vendorId!!).collection("grupOpsi").document(i)
                        grupOpsiRef.get().addOnSuccessListener { snapshot ->
                            if (snapshot != null) {
                                val grupOpsiModel = GrupOpsi()
                                grupOpsiModel.id = i
                                grupOpsiModel.nama = snapshot.data?.get("nama").toString()
                                grupOpsiList.add(grupOpsiModel)

                                grupOpsiList.sortBy {
                                    it.nama
                                }

                                grupOpsiAdapter.setItems(grupOpsiList)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupView() {
        recyclerView = binding.rvGrupOpsi
        recyclerView.layoutManager = LinearLayoutManager(this)
        grupOpsiAdapter = GrupOpsiAdapter(this, vendorId!!, opsiListCheck, btnPesan, grupOpsiId!!, menuPrice, etJumlah)
        recyclerView.adapter = grupOpsiAdapter
    }

    private fun editJumlah() {
        ibLess.setOnClickListener {
            val jumlah = etJumlah.text.toString().toInt()
            val less = jumlah - 1
            etJumlah.setText(less.toString())
        }
        ibMore.setOnClickListener {
            val jumlah = etJumlah.text.toString().toInt()
            val more = jumlah + 1
            etJumlah.setText(more.toString())
        }
    }

    private fun pesan() {
        btnPesan.setOnClickListener{
            Log.d("opsiList", opsiListCheck.toString())
            //TODO: 3. KIRIM NILAI KE PEMESANAN ACTIVITY
        }
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}