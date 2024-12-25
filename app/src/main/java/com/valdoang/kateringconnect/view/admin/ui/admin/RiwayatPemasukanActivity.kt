package com.valdoang.kateringconnect.view.admin.ui.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.adapter.MutasiAdapter
import com.valdoang.kateringconnect.databinding.ActivityRiwayatPemasukanBinding
import com.valdoang.kateringconnect.model.Mutasi
import com.valdoang.kateringconnect.utils.Cons

class RiwayatPemasukanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRiwayatPemasukanBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var mutasiList: ArrayList<Mutasi> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var mutasiAdapter: MutasiAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatPemasukanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        progressBar = binding.progressBar

        setupView()
        setupData()
        setupAction()
    }

    private fun setupView() {
        recyclerView = binding.rvRiwayatPemasukan
        recyclerView.layoutManager = LinearLayoutManager(this)
        mutasiAdapter = MutasiAdapter(this)
        recyclerView.adapter = mutasiAdapter
    }

    private fun setupData() {
        progressBar.visibility = View.VISIBLE
        val pemasukanRef = db.collection("user").document(Cons.ADMIN_ID).collection("pemasukan")
        pemasukanRef.addSnapshotListener { mutasiSnapshot, _ ->
            progressBar.visibility = View.GONE
            if (mutasiSnapshot != null) {
                mutasiList.clear()
                for (data in mutasiSnapshot.documents) {
                    val mutasi: Mutasi? = data.toObject(Mutasi::class.java)
                    if (mutasi != null) {
                        mutasi.id = data.id
                        mutasiList.add(mutasi)
                    }
                }

                mutasiList.sortByDescending {
                    it.tanggal
                }

                mutasiAdapter.setItems(mutasiList)

                if (mutasiList.isEmpty()) {
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

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}