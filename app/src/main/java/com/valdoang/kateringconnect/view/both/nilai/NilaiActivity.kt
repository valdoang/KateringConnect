package com.valdoang.kateringconnect.view.both.nilai

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.valdoang.kateringconnect.R
import com.valdoang.kateringconnect.adapter.NilaiAdapter
import com.valdoang.kateringconnect.databinding.ActivityNilaiBinding
import com.valdoang.kateringconnect.model.Nilai
import com.valdoang.kateringconnect.utils.Cons
import com.valdoang.kateringconnect.utils.roundOffDecimal

class NilaiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNilaiBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var nilaiAdapter: NilaiAdapter
    private lateinit var nilaiList: ArrayList<Nilai>
    private lateinit var rbStar: RatingBar
    private lateinit var tvStarSize: TextView
    private lateinit var tvStar: TextView
    private lateinit var progressBar: ProgressBar
    private var vendorId: String? = null
    private var totalNilai = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNilaiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        
        firebaseAuth = Firebase.auth
        nilaiList = arrayListOf()

        vendorId = intent.getStringExtra(Cons.EXTRA_ID)

        progressBar = binding.progressBar
        rbStar = binding.rbStar
        tvStarSize = binding.tvStarSize
        tvStar = binding.tvStar
        
        setupNilai()
        setupView()
        setupAction()
    }
    
    private fun setupNilai() {
        progressBar.visibility = View.VISIBLE
        val nilaiRef = db.collection("nilai").whereEqualTo("vendorId", vendorId)
        nilaiRef.addSnapshotListener { snapshot,_ ->
            progressBar.visibility = View.GONE
            if (snapshot != null) {
                nilaiList.clear()
                for (data in snapshot.documents) {
                    val nilai: Nilai? = data.toObject(Nilai::class.java)
                    if (nilai != null) {
                        nilaiList.add(nilai)
                    }
                }

                nilaiAdapter.setItems(nilaiList)

                for (i in nilaiList) {
                    val nilai = i.nilai?.toDouble()
                    if (nilai != null) {
                        totalNilai += nilai
                    }
                }

                val sizeNilai = nilaiList.size
                val nilaiStar = totalNilai/sizeNilai

                if (sizeNilai == 0) {
                    tvStar.text = "0.0"
                }
                else {
                    tvStar.text = nilaiStar.roundOffDecimal()
                }

                rbStar.rating = nilaiStar.toFloat()
                tvStarSize.text = getString(R.string.jumlah_penilaian, sizeNilai.toString())

                if (nilaiList.isEmpty()) {
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

    private fun setupView() {
        recyclerView = binding.rvNilai
        recyclerView.layoutManager = LinearLayoutManager(this)

        nilaiAdapter = NilaiAdapter(this)
        recyclerView.adapter = nilaiAdapter
        nilaiAdapter.setItems(nilaiList)
    }

    private fun setupAction() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}