package com.valdoang.kateringconnect.view.both.kcwallet

import android.content.Intent
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
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.GalleryAdapter
import com.valdoang.kateringconnect.adapter.MutasiAdapter
import com.valdoang.kateringconnect.databinding.ActivityMutasiBinding
import com.valdoang.kateringconnect.model.Gallery
import com.valdoang.kateringconnect.model.Mutasi
import com.valdoang.kateringconnect.view.vendor.galeri.AddGaleriActivity

class MutasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMutasiBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var userId = ""
    private var mutasiList: ArrayList<Mutasi> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var mutasiAdapter: MutasiAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMutasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        firebaseAuth = Firebase.auth
        userId = firebaseAuth.currentUser!!.uid
        progressBar = binding.progressBar

        setupView()
        setupData()
        setupAction()
    }

    private fun setupView() {
        recyclerView = binding.rvMutasi
        recyclerView.layoutManager = LinearLayoutManager(this)
        mutasiAdapter = MutasiAdapter(this)
        recyclerView.adapter = mutasiAdapter
    }

    private fun setupData() {
        progressBar.visibility = View.VISIBLE
        val mutasiRef = db.collection("user").document(userId).collection("mutasi")
        mutasiRef.addSnapshotListener { mutasiSnapshot, _ ->
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