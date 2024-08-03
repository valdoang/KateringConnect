package com.valdoang.kateringconnect.view.user.keranjang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.AllKeranjangAdapter
import com.valdoang.kateringconnect.databinding.ActivityAllKeranjangBinding
import com.valdoang.kateringconnect.model.AllKeranjang
import com.valdoang.kateringconnect.model.Keranjang
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.view.user.pemesanan.PemesananActivity

class AllKeranjangActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllKeranjangBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var allKeranjangAdapter: AllKeranjangAdapter
    private var allKeranjangList: ArrayList<AllKeranjang> = ArrayList()
    private var db = Firebase.firestore
    private lateinit var firebaseAuth: FirebaseAuth
    private var userId = ""
    private var alamatId: String? = null
    private lateinit var tvEdit: TextView
    private lateinit var clEdit: ConstraintLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var btnHapus: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllKeranjangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        
        firebaseAuth = Firebase.auth
        userId = firebaseAuth.currentUser!!.uid

        alamatId = intent.getStringExtra(Cons.EXTRA_ID)
        tvEdit = binding.tvEdit
        clEdit = binding.clEdit
        progressBar = binding.progressBar
        btnHapus = binding.btnHapus

        setupAction()
        setupView(false, pilihSemua = false)
        setupAllKeranjang()
        setupEditKeranjang()
        deleteKeranjang()
    }
    
    private fun setupAllKeranjang() {
        progressBar.visibility = View.VISIBLE
        val allKeranjangRef = db.collection("user").document(userId).collection("keranjang")
        allKeranjangRef.addSnapshotListener { snapshot, _ ->
            progressBar.visibility = View.GONE
            if (snapshot != null) {
                allKeranjangList.clear()
                for (data in snapshot.documents) {
                    //TODO: NTAR COBA PAKE DATA CLASS VENDOR AJA
                    val allKeranjang: AllKeranjang? = data.toObject(AllKeranjang::class.java)
                    if (allKeranjang != null) {
                        allKeranjang.vendorId = data.id
                        allKeranjangList.add(allKeranjang)

                        allKeranjangAdapter.setItems(allKeranjangList)
                        allKeranjangAdapter.setOnItemClickCallback(object : AllKeranjangAdapter.OnItemClickCallback{
                            override fun onItemClicked(data: AllKeranjang) {
                                val intent = Intent(this@AllKeranjangActivity, PemesananActivity::class.java)
                                intent.putExtra(Cons.EXTRA_ID, data.vendorId)
                                intent.putExtra(Cons.EXTRA_FOURTH_ID, alamatId)
                                //TODO: PIKIRKAN CARA MENENTUKAN ONGKIRNYA
                                intent.putExtra(Cons.EXTRA_ONGKIR, "18000")
                                startActivity(intent)
                            }

                        })
                    }
                }
            }
        }
    }

    private fun setupEditKeranjang() {
        val checkListener =
            CompoundButton.OnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    setupView(true, pilihSemua = true)
                    //TODO: AMBIL JUMLAH CHECK DAN TAMPILKAN PADA BTN HAPUS
                    btnHapus.text = getString(R.string.hapus_jumlah_keranjang)
                } else {
                    setupView(true, pilihSemua = false)
                    btnHapus.text = getString(R.string.hapus)
                }
            }
        binding.cbPilihSemua.setOnCheckedChangeListener(checkListener)
        tvEdit.setOnClickListener {
            when (tvEdit.text) {
                getString(R.string.atur) -> {
                    tvEdit.text = getString(R.string.batalkan)
                    setupView(true, pilihSemua = false)
                    clEdit.visibility = View.VISIBLE
                }
                getString(R.string.batalkan) -> {
                    tvEdit.text = getString(R.string.atur)
                    setupView(false, pilihSemua = false)
                    clEdit.visibility = View.GONE
                }
            }

        }
    }

    private fun deleteKeranjang() {
        btnHapus.setOnClickListener {
            //TODO: HAPUS KERANJANG
        }
    }
    
    private fun setupView(edit: Boolean, pilihSemua: Boolean) {
        recyclerView = binding.rvKeranjang
        recyclerView.layoutManager = LinearLayoutManager(this)
        //TODO: KIRIM BTN HAPUS UNTUK DIUBAH TEXTNYA KETIKA CHECK BOX DITEKAN
        allKeranjangAdapter = AllKeranjangAdapter(this, edit, pilihSemua)
        recyclerView.adapter = allKeranjangAdapter
        allKeranjangAdapter.setItems(allKeranjangList)
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}