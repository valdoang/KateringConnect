package com.valdoang.kateringconnect.view.vendor.menu

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.MenuAdapter
import com.valdoang.kateringconnect.databinding.ActivityMenuBinding
import com.valdoang.kateringconnect.model.Menu
import com.valdoang.kateringconnect.view.user.menu.DetailMenuFragment

class VendorMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var tvKategori: TextView
    private lateinit var tvName: TextView
    private lateinit var ivPhoto: ImageView
    private var jenis: String? = null
    private var foto: String? = null
    private var nama: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var menuList: ArrayList<Menu>
    private lateinit var menuAdapter: MenuAdapter
    private var userJenis = ""
    private lateinit var progressBar: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        jenis = intent.getStringExtra(EXTRA_JENIS)
        foto = intent.getStringExtra(EXTRA_FOTO)
        nama = intent.getStringExtra(EXTRA_NAMA)

        tvKategori = binding.tvKategori
        tvName = binding.tvVendorName
        ivPhoto = binding.ivVendorPhoto
        progressBar = binding.progressBar

        firebaseAuth = Firebase.auth
        menuList = arrayListOf()

        setupAction()
        setupTv()
        setupProfile()
        setupView()
        setupMenu()
    }

    private fun setupProfile() {
        Glide.with(this).load(foto).error(R.drawable.default_profile).into(ivPhoto)
        tvName.text = nama
    }

    private fun setupTv() {
        when (jenis.toString()) {
            getString(R.string.nasi_kotak) -> {
                tvKategori.text = getString(R.string.kategori_nasi_kuning)
            }
            getString(R.string.tumpeng) -> {
                tvKategori.text = getString(R.string.kategori_tumpeng)
            }
            getString(R.string.prasmanan) -> {
                tvKategori.text = getString(R.string.kategori_prasmanan)
            }
        }
    }

    private fun setupMenu() {
        progressBar.visibility = View.VISIBLE
        var userId = firebaseAuth.currentUser!!.uid
        db.collection("user").document(userId).get()
            .addOnSuccessListener { document ->
                progressBar.visibility = View.GONE
                if (document != null) {
                    userJenis = document.data?.get("jenis").toString()
                    if (userJenis == getString(R.string.pembeli)) {
                        userId = intent.getStringExtra(EXTRA_ID).toString()
                    }
                    val menuRef = db.collection("menu").whereEqualTo("userId", userId).whereEqualTo("jenis", jenis)
                    menuRef.addSnapshotListener{ snapshot,_ ->
                        if (snapshot != null) {
                            Log.d(TAG, "Current data: ${snapshot.documents}")
                            menuList.clear()
                            for (data in snapshot.documents) {
                                val menu: Menu? = data.toObject(Menu::class.java)
                                if (menu != null) {
                                    menu.id = data.id
                                    menuList.add(menu)
                                }
                            }

                            menuAdapter.setItems(menuList)
                            menuAdapter.setOnItemClickCallback(object :
                                MenuAdapter.OnItemClickCallback {
                                override fun onItemClicked(data: Menu) {
                                    if (userJenis == getString(R.string.pembeli)) {
                                        val dialog = DetailMenuFragment()
                                        dialog.show(supportFragmentManager, "detailDialog")
                                    }
                                    else if (userJenis == getString(R.string.vendor)) {
                                        val intent =Intent(this@VendorMenuActivity, EditMenuActivity::class.java)
                                        intent.putExtra(EditMenuActivity.EXTRA_ID, data.id)
                                        startActivity(intent)
                                    }
                                }
                            })

                            if (menuList.isEmpty()) {
                                binding.noDataAnimation.visibility = View.VISIBLE
                                binding.tvNoData.visibility = View.VISIBLE
                            }
                            else {
                                binding.noDataAnimation.visibility = View.GONE
                                binding.tvNoData.visibility = View.GONE
                            }
                        }
                    }
                }
            }
    }

    private fun setupView() {
        recyclerView = binding.rvMenu
        recyclerView.layoutManager = GridLayoutManager(this,2)

        menuAdapter = MenuAdapter(this)
        recyclerView.adapter = menuAdapter
        menuAdapter.setItems(menuList)
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            onBackPressed()
        }
    }

    companion object {
        const val EXTRA_JENIS = "extra_jenis"
        const val EXTRA_FOTO = "extra_foto"
        const val EXTRA_NAMA = "extra_nama"
        const val EXTRA_ID = "extra_id"
    }
}